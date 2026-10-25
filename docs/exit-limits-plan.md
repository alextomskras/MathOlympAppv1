# План: exit-поведение и лимиты (master, 3129de6)

Разбор кода выявил конкретные дефекты. Ниже — что чиним и в каком порядке.

## A. Lимиты

1. **Сброс лимитов при открытии раздела** (критично).
   `RAZDELFragment` при клике по разделу кладёт в бандл жёстко `solutionlimits="3"`, `hintlimits="3"`
   (`RAZDELFragment.java:170-171`), а `ZadachaFragment.onCreate` сразу пишет их в Firebase
   (`ZadachaFragment.java:90-91`). Итог: каждый заход в раздел возвращает лимиты к 3,
   потраченные подсказки/решения "восстанавливаются".
   Чиним: передаём реальные сохранённые лимиты; `ZadachaFragment` не пишет бандл-лимита в Firebase.

2. **Нулевые лимиты не сохраняются** (критично).
   `ZadachaFragment.sharedPreffsSaveHintLimits` пропускает запись при 0
   (`ZadachaFragment.java:228`) — после исчерпания подсказок значение возвращается к старому.
   Чиним: сохраняем любое значение, включая 0.

3. **Два несовпадающих набора ключей** (критично).
   Экраны читают лимиты как Int из ключей `solution_limits`/`hint_limits`
   (`SharedPreffUtils.java:59-73`), а вход сохраняет их как String под
   `solutionlimits`/`hintlimits` (`LoginFragment.java:426-427`). Ключи разные,
   поэтому после входа Int-ключи пустые → кнопки подсказки/решения серые и неактивные.
   Чиним: вход (обычный и авто-вход) заполняет Int-ключи, `RAZDELFragment` тоже.

4. **Дефолты теряются, 0 неотличим от "не задано"** (средне).
   `getDataFromSharedPreferences` возвращает 0, когда ключа нет
   (`SharedPreffUtils.java:82-87`) — "исчерпано" и "ни разу не сохранялось" одинаковы.
   Чиним: чтение по `contains(key)` с дефолтами 1 и 3.

5. **Слушатели Firebase навсегда** (высоко).
   `MyArrayList.firebaseGetSolutionLimits`, `firebaseGetHintLimits`, `firebaseLoadSolutionLimits`
   используют `addValueEventListener` (`MyArrayList.java:165,201,231,293`) — слушатель не снимается,
   каждая посещённая страница добавляет новый, и один и тот же узел читается многократно,
   каждая читалка пишет в SharedPreferences.
   Чиним: `addListenerForSingleValueEvent`.

6. **Падение на разборе значения из базы** (высоко).
   `Integer.valueOf((String) dataSnapshot.getValue())` (`MyArrayList.java:172,208,238,299`) —
   `NumberFormatException` при нечисловом/пустом значении; `firebaseLoadSolutionLimits`
   NPE при `user == null` (`MyArrayList.java:158-162`).
   Чиним: безопасный парсинг + guard'ы.

7. **Цвета кнопок не обновляются после списания** (средне).
   `ScrollingFragmentViewModel.setButtonColors` присваивает поля самим себе
   (`ScrollingFragmentViewModel.java:157-158`) и цвета ставятся только при показе диалога.
   Чиним: убираем self-assignment, после списания лимита перекрашиваем кнопки.

8. **Нет реакции при исчерпанных лимитах** (средне).
   При 0 кнопка молча выходит (`ScrollingFragment.java:404-406,417-419`).
   Чиним: видимое сообщение "лимит исчерпан".

9. **Двойная загрузка лимитов** (мелко).
   `ZadachaFragment.onViewCreated` вызывает легатский путь и callback-путь
   (`ZadachaFragment.java:182,186`) — один и тот же узел читается дважды.
   Чиним: один путь.

10. **Уход в минус** (мелко).
    Декrement не ограничен снизу — чиним: не ниже 0.

## B. Exit

1. **Меню "Exit" не проходит через `logout()`** (критично).
   `MyMenuInflater.inflateMenu` (`MyMenuInflater.java:28-36`) и
   `MyMenuInflater.RazdelinflateMenu` (`MyMenuInflater.java:62-70`) зовут
   `FirebaseAuth.signOut()` напрямую. Не останавливается `SessionManager`
   (планировщик обновляет токен и после выхода), слушатель AuthState
   сразу зовёт `signInAnonymously()` — пользователь "возвращается" в авторизованное
   состояние; локальные данные (username, uid, лимиты, счёт) не очищаются;
   бэкстек не чистится (`popBackStack(loginFragment, false)` при отсутствии
   loginFragment в стеке ничего не снимает).
   Чиним: оба пункта меню ведут на `MainActivity.logout()` (там уже есть
   снятие слушателя, остановка SessionManager, очистка обоих хранилищ).

2. **Выход оставляет авторизованные экраны в бэкстеке** (критично).
   `MainActivity.navigateToLogin()` (`MainActivity.java:459-473`) снимает
   `loginFragment` — его там нет (на входе снят inclusive), и `navigate(...,
   popUpTo(startDestination, inclusive=false))` тоже ничего не снимает.
   После выхода Back возвращает на экраны решённой задачи при signed-out сессии.
   Чиним: снять стек до верхнего экрана приложения inclusive, переходить
   на login с `popUpTo(RAZDELFragment, inclusive=true)` и `launchSingleTop`,
   чтобы в стеке остался только экран входа (Back закрывает приложение).

3. **Порядок flag/слушателя при выходе** (средне).
   `navigateToLogin()` сбрасывает `isLoggingOut` и вешает слушатель до/вместе
   с навигацией (`MainActivity.java:470-472`) — анонимный вход может произойти
   до завершения навигации. Чиним: сначала навигация, потом флаг и слушатель;
   guard на `authStateListener != null`.

4. **`SessionManager.stop()` блокирует поток выхода на 5 секунд** (средне, "замирание кнопки exit").
   `awaitTermination(5s)` (`SessionManager.java:122-128`) выполняется на UI-потоке
   из `logout()`. Чиним: shutdown без ожидания.

5. **`SessionManager.stop()` при незапущенном планировщике** (средне).
   `scheduler.shutdown()` без null-guard (`SessionManager.java:119`) — NPE, если
   выход случился до start(). Чиним: guard.

## Порядок работ

1. `SharedPreffUtils` — дефолты и различие "нет ключа"/"0".
2. `MyArrayList` — single-value слушатели, безопасный парсинг, guard'ы.
3. `LoginFragment` — заполнение Int-ключей лимитов при входе и авто-входе.
4. `RAZDELFragment` — реальные лимиты в бандл, посевprefs, без хардкода 3.
5. `ZadachaFragment` — без записи бандла в Firebase, сохранение 0, один путь загрузки.
6. `ScrollingFragment` + ViewModel — перекраска после списания, сообщение при исчерпании, нижняя граница 0.
7. `MyMenuInflater` — Exit через `MainActivity.logout()`.
8. `MainActivity` + `SessionManager` — чистый бэкстек после выхода, порядок flag/слушателя, неза-blocking stop.

Проверка: компиляция `assembleDebug` (JDK 17 + Android SDK на этой машине), затем
сверка diff, коммиты и push в master.
