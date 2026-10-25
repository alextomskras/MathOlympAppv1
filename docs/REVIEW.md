# Code Review: MathOlympAppv1

Дата: 2026-10-23. Полный статический анализ по вопросам: выход из приложения,
зависания, статистика, SharedPreferences vs Firebase, офлайн, картинки/Storage.

## Итоговый вердикт

Рабочий «текстовый» цикл (вход → разделы → список задач → ответ → +10 очков)
работает, если в Realtime DB есть данные `Razdel/*` и права на чтение.
Но: **кнопка Exit в меню реально не выходит ни из приложения, ни из аккаунта**;
**картинки не загружаются никогда** (сломан код + Storage забанен);
**лимиты подсказок/решений сбрасываются на 3 при каждом переходе в раздел**;
**счёт/лимиты живут в трёх SharedPreferences с несовпадающими ключами**.
Сборки на этой машине проверить нельзя — установлен только JRE 8,
Gradle 8.0 и AGP 7.3.0 требуют JDK 11+.

---

## 1. Кнопка Exit, выход из приложения, зависания

**Что есть:**
- Кнопка «Exit» в меню экранов ScrollingFragment и RAZDELFragment
  (`MyMenuInflater.java:25-36, 59-70`; строка `allertDialogNegativeButton = "Exit"`).
- Настройки → «Выйти из аккаунта» (`SettingsFragment.java:26-45`) → `MainActivity.logout()`.
- На экране входа есть `btnsignout` (`LoginFragment.java:135, 260-283`).
- Выход из приложения: только кнопка Back на экране входа (стандартный `finish()`
  активности; явного `finish()`/`System.exit` нигде нет).

**Главный дефект — меню Exit:**
1. `MyMenuInflater` делает `popBackStack(loginFragment, false)`, `navigate(loginFragment)`,
   `FirebaseAuth.signOut()` — без флага `isLoggingOut`, без остановки SessionManager,
   без очистки prefs (`MyMenuInflater.java:29-35`).
2. Активный `AuthStateListener` в `MainActivity.java:200-211` после signOut мгновенно
   делает `signInAnonymously()` — пользователь снова «авторизован».
3. `LoginFragment.onViewCreated` (103-115) видит `getCurrentUser() != null` и сразу
   автологином возвращает на RAZDELFragment.
   **Итог: нажатие Exit → короткий переход и пользователь остаётся в приложении.**
   Оправдан только как «вернуться к списку», выход из аккаунта через него не работает.
4. Prefs (score, лимиты, uid, username) не очищаются → при следующем входе другого
   аккаунта старые очки/лимитыremain (`user_score` в secure prefs).

**Правильный путь есть** — `MainActivity.logout()` (407-451): флаг `isLoggingOut`,
снятие слушателя до signOut, остановка singleton SessionManager, очистка обоих
хранилищ, очистка back stack, возврат слушателя. Работает корректно, проверен
по коду.

**Зависания:**
- Direct UI-блокеров нет: все обращения к Firebase асинхронные, `apply()` в prefs,
  нет циклов на главном потоке. ANR не предвидится.
- SessionManager (`SessionManager.java:73-94`) — scheduler не останавливается при
  выходе через меню Exit; фоновый поток refresh каждые 50 мин живёт до kill процесса
  (не блокирует UI, но «грязного» завершения нет).
- `NetworkManager.java:37-61`: `registerNetworkCallback` никогда не
  `unregisterNetworkCallback` — утечка на весь процесс.
- EncryptedSharedPreferences инициализируется на главном потоке
  (`SecureSharedPrefsUtils.java:29-51`) — 100-300 мс на первом обращении, терпимо.
- Диалог офлайна (`MainActivity.java:320-342`) неуказываемый, блокирует UI, но
  снимается при восстановлении сети — не зависание.

**Рекомендация:** меню Exit направить в `MainActivity.logout()` (единая точка),
добавить настоящий выход из приложения (на экране входа: Back → finish,
либо двойной Back с toast), `unregisterNetworkCallback` в `onDestroy()`.

---

## 2. Статистика игрока: SharedPreferences + Firebase (оба)

**Очки (`checkAnswer`):** `ScrollingFragment.java:317-319` —load из secure prefs
(`user_score`) +10, save в secure prefs и в RTDB
`Users/{uid}/userscore` (`FirebaseUserScoreManager.java:17-31`, значение — строка).
**Записанные задачи:** RTDB `Users/{uid}/completed_tasks/{razdel}`
как map "0..n" (MyArrayList.java:32-73, 130-154), плюс static список `MyArrayList.list`.
**Лимиты:** decrement в secure prefs (`ScrollingFragment.java:403-410, 437-447`) +
write в RTDB `hintlimits`/`solutionlimits` (строкой).
**При регистрации** (`RegisterFragment.java:396-446`) создаётся auth-пользователь
и пишется `Users/{uid}/{username, token_id}`; score/лимиты не инициализируются —
первый вход берёт defaults 1/3 (`LoginFragment.java:375-399`).

**Проблемы:**
1. **Сброс лимитов**: `RAZDELFragment.onItemClick` кладёт в bundle жёстко `3/3`
   (`RAZDELFragment.java:169-171`), `ZadachaFragment.onCreate` (80-120) пишет их
   в RTDB, и `firebaseGetSolutionLimits` (MyArrayList.java:191-232) читает RTDB и
   перетирает secure prefs → потраченные подсказки восстанавливаются при каждом
   переходе. Экономика ломается.
2. **Несовпадение ключей prefs**: `SharedPreffUtils2` («MySharedPreferences») читает
   ключи `solutionlimits`/`hintlimits` (RAZDELFragment.onStart, 222-231), а пишутся
   `solution_limits`/`hint_limits` (`SharedPreffUtils.java`) → ветка никогда не
   срабатывает; score из `zadacha_score` в action bar не обновляется реальной
   логикой (пишется в secure `user_score`).
3. **Три параллельных хранилища** (SecureSharedPrefsUtils, SharedPreffUtils2,
   SharedPreffUtils со static-полем) с пересекающимися ключами — легко
   расходятся; статика `SharedPreffUtils.sharedPreferences` живёт глобально.
4. `MyArrayList.saveArrayCompletedTasksToFirebase` (61-69) вызывает `setValue`
   внутри цикла по всем элементам (n записи на одно добавление).
5. Score — источник истины двойной: локальный +10 пишется поверх RTDB; конфликт
   при multi-device не разрешается (нужен `userscore` из DB при login).

**Ответ на вопрос:** статистика хранится **и в SharedPreferences (кэш), и в
Realtime Database ( Users/{uid}/**); RTDB — персистентное хранилище, prefs —
локальный кэш, и синхронизация между ними сейчас сломана (п.1-3). Firestore
не используется вообще (в бандлах только имена «firestore», фактически RTDB).

---

## 3. Реакция на пропажу интернета

- `NetworkManager` (utils/NetworkManager.java) через ConnectivityManager callback
  ловит DISCONNECTED → toast + неуказываемый диалог «Нет подключения» с кнопкой
  «Повторить»; при возврате сети диалог сам dismiss'ится
  (`MainActivity.java:290-352`).
- RTDB offline persistence включена (`MyApp.java:18`): списки разделов/задач
  читаются из кэша, записи очков/лимитов/`completed_tasks` стоят в очереди и
  синхронизируются при восстановлении сети.
- Storage при пропаже сети (и при bans — ниже) даёт failures в пустые
  failure-listeners (adapter 694-701; ViewModel 147-150) → тихо, без crash.
- Auth-запросы без сети: `signInWithEmailAndPassword` завершается
  `TASK_CANCELED`/timeout → toast «Login failed»; UI не блокируется.
- Итог: базовая реакция есть и не зависает; диалог стоит «поверх» всего —
  стоит сделать его cancelable и компактным баннером.

---

## 4. Картинки к задачам и ответам

**Как задумано:** файлы в Storage `gs://matholymp1.appspot.com/answersimages/*`
(ответ) и `solutionimages/*` (решение). Адаптер списка делал `listAll()` и
складывал список в bundle; ScrollingFragment по индексу `zadacha_id-1` доставал
имя файла и грузил через Coil.

**Фактически не работает, двойной поломкой:**
1. **Код:** `ZadachiRecyclerViewAdapter.java:687, 692` складывает
   `String.valueOf(item)`, где `item` — объект `StorageReference` (не путь, не имя).
   Строка вида `com.google.firebase.storage.StorageReference@...` — `split("/")`
   не даёт `parts[4]` (`ScrollingFragmentViewModel.java:132-134`) →
   IndexOutOfBounds ловится catch (151-153) и **картинка никогда не загружается**.
   В старой (закоммиченной) версии было `item.getName()` — корректно.
   Класс `FirebaseImageLoader.java:21` вообще создаёт
   `new ScrollingFragment()` вне FragmentManager (detached, getContext()==null) —
   dead-code с гарантированным crash, если его когда-нибудь вызовут.
2. **Storage забанен (Free/Spark):** `app.appspot.com` bucket недоступен без Blaze;
   `FirebaseStorage.getInstance("gs://matholymp1.appspot.com")`
   (`FirebaseUserScoreManager.java:66`, ScrollingFragment 183) — `listAll()` и
   `getDownloadUrl()` падают. Даже при починке кода картинки не придут.
3. **Согласование «задача ↔ файл»:** соответствие по позиции в
   алфавитном `listAll()` (`list.get(zadacha_id-1)`) случайно; надёжный ключ —
   имя файла по номеру задачи (`spaceRef.child("answersimages/answer"+zadacha_id)`
   создан, ViewModel.java:127, но для загрузки не используется).
4. В списке задач прогресс-бар показывается, пока список файлов пуст
   (adapter 895-899) — крутится вечно.
5. `firebase-ui-storage:7.2.0` подключён, но Glide не используется — зависимость
   лишняя.

**Рекомендация:** (а) завести Storage на Blaze, либо перенести картинки
(сжатые base64 в RTDB / URL на обычный http-хост); (б) хранить
`item.getPath()/getName()`, имя файла = `answer{N}.png`, загрузка
`child("answersimages/answer"+zadacha_id+".png")` без split; (в) удалить
FirebaseImageLoader; (г) failure-пути показывать пользователю, а не глотать.

---

## 5. Сборка

- Агенты 7.3.0, Gradle wrapper 8.0, Kotlin 1.9.22, minSdk 21, target 34,
  `google-services.json` в `app/` — конфигурация корректна.
- **На этой машине собрать нельзя**: установлен только JRE 8.0_471; JBR Android
  Studio неполон (`jbr/lib` без `jvm.cfg`); JDK 11+ отсутствует.
  Требуется JDK 17 (напр. Temurin) → `.\gradlew :app:assembleDebug`
  (проверено: сборка падает именно на JDK).
- BoM firebase 31.1.1 (2022) устарел, security-crypto `1.1.0-alpha06` содержит
  известный crash master-key гонки при первом запуске → поднимите до
  `1.1.0-alpha06`→`1.1.0-alpha07+`/`1.1.0-beta+`.
- Дублирующиеся `firebase-core` (по BoM и явная 21.1.1) — мелочь.
- Закоммиченный мусор: два `PlaceholderContent`, полностью закомментированные
  старые MainActivity/RegisterFragment/ZadachiRecyclerViewAdapter (~1900 строк
  комментариев) — чистить.

---

## ## 6. Ветки Git: что слиты, а что нет (проверено по GitHub API)

**master заморожен на 11.04.2026** (tip d379c8d, PR #5 gradle-jvm). В master
слиты только PR #1–#5. Все последующие 40+ PR (#6–#48, «qwen-chat coder»,
апрель–сентябрь 2026) слиты в боковые цепочки веток, которые никогда не
вливались в master. Локальный клон = актуальный master.

Самые насыщенные фиксами ветки (кончики цепочек):
- `add-new-auth-and-firebaseauth-check` (823e794, 21.09.2026) — email-валидация
  входа (`InputValidator`, проверка NoSQL-инъекций), тёмная тема через
  `AppCompatDelegate`, ActionBar-заголовки по фрагментам, Coil вместо Glide,
  signout-кнопка чистит prefs.
- `try-check-crash-over-context` (а1f22b9) — context-safe фиксы.
- `заголовок-для-задачи`/`fix-back-button-in-tasks` — заголовки, переход
  назад из списка задач (bundle с `MyArgRazdel_id`).
- `gradle-jvm-compatibility` (PR #5, ЕДИНСТВЕННИК в master) — `jvm.config`,
  `gradleJvm=17`.

**Главное: ключевые баги НЕ исправлены ни в одной ветке.** В кончике
`add-new-auth` (самом свежем коде) проверено:
- `MyMenuInflater` — Exit = popBackStack+navigate+signOut, тот же обход
  `isLoggingOut`; `AuthStateListener` (главным) в ветке вообще без флага.
- `MainActivity.logout()` в ветке слабее master: без остановки SessionManager,
  без очистке EncryptedSharedPreferences, listener активен → signOut →
  анонимный вход.
- `ScrollingFragmentViewModel.setFirebaseImage` — `parts[4]` на
  `String.valueOf(StorageReference)` (adapter) — картинки по-прежнему никогда
  не грузятся; фикс `item.getName()` живёт только в закомментированных
  старых версиях адаптера.
- `RAZDELFragment.onItemClick` — bundle жёстко `3/3`, сброс лимитов.
- `MyArrayList`, три prefs-хранилища — без изменений.

Вывод: «в ветках всё поправлено» — нет. Ветки добавляют улучшения
(валидация, тема, Coil, навигация), базовые поломки (exit, картинки,
лимиты, prefs) живут во всех ветках.

**Сборка Android Studio**: да, Studio собирается — у Studio свой
встроенный JDK 17 (jbr), master с PR #5 содержит `jvm.config` и
`gradleJvm=17`. Моё «собрать нельзя» касалось только CLI в песочнице
(нет доступа к Program Files, нет внешнего JDK) — это ограничение
проверки, а не проекта.

---

## Приоритет исправлений
1. Exit-меню → единый `MainActivity.logout()`; настоящий выход из приложения.
2. Прекратить сброс лимитов: bundle = реальные лимиты (secure prefs / RTDB),
   `firebaseGetSolutionLimits` → `addListenerForSingleValueEvent`.
3. Картинки: Blaze или перенос хранилища; `item.getName()`; имя файла по
   номеру задачи; удалить `FirebaseImageLoader`; показывать ошибки загрузки.
4. Единое хранилище prefs: один помощник, один набор ключей, очистка при любом
   logout; score брать из RTDB при login.
5. `unregisterNetworkCallback`, cancelable offline-баннер.
6. JDK 17 + сборка, поднятие BoM/security-crypto.
