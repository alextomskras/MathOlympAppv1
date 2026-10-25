# MinIO-прокси: картинки задач по сквозной нумерации

## Что это

Приложение умеет грузить картинки ответов/решений из MinIO через FastAPI-прокси
на Render. Прокси защищён токеном: неподписанный POST-запрос не принимается.
Cron-задание раз в минуту шлёт подписанный POST `/ping`, чтобы Render не усыплял
контейнер.

## Сквозная нумерация

Firebase RTDB нумерации задач по всем разделам не хранит (`Zadachi/<раздел>/zadacha_N`,
номера только внутри раздела). Сквозной номер считается на клиенте
(`ScrollingFragmentViewModel.setRemoteImage`):

```
global = sum(число задач во всех разделах с номером меньше текущего) + номер задачи в разделе
```

Нумерация разделов — цифра в конце ключа: `razdel_3` -> 3. В MinIO объекты лежат
плоско, без отдельных папок по разделам:

```
tasks/<global>/answer.jpg
tasks/<global>/solution.jpg
```

## Контракт запроса (Android -> прокси)

- `POST` на URL из настройки `remote_proxy_base_url`
- Заголовок `X-Auth: <unix-секунд>:<hex HMAC-SHA256(secret, ts + "\n" + body)>`
- Тело: `{"global": 42, "kind": "answer"}` (kind: `answer` | `solution`)
- Ответ: `{"url": "<presigned GET на 1 час>", ...}`; приложение грузит url через Coil.

Секрет (`remote_proxy_secret`) совпадает с `MINIO_PROXY_SECRET` на сервере.
Оба значения задаются в настройках приложения (экран Settings: URL, секрет,
переключатель «MinIO proxy: включён»). По умолчанию прокси выключен — картинки
идут из Firebase Storage (путь `parts[4]` остаётся резервным путём).

## Сервер

`proxy/fastapi_minio_proxy.py` — эталонная реализация проверки подписи
(окно времени `TS_WINDOW`, по умолчанию 300 с). Запуск на Render:

```
pip install fastapi uvicorn minio
uvicorn fastapi_minio_proxy:app --host 0.0.0.0 --port 8088
ENV: MINIO_PROXY_SECRET, MINIO_ENDPOINT, MINIO_ACCESS_KEY, MINIO_SECRET_KEY, MINIO_BUCKET, MINIO_SECURE
```

Cron (пример с внешнего cron, раз в минуту, подпись в cron-скрипте):

```
0 * * * *  python3 /path/to/cron_ping.py
# cron_ping.py: ts=...; body=""; sig=hmac(secret, ts+"\n"+body);
# curl -X POST https://<url>/ping -H "X-Auth: ts:sig"
```

## Загрузка в MinIO (скрипт мигрирует старые картинки)

Объекты должны называться `tasks/<global>/answer.jpg` и `tasks/<global>/solution.jpg`,
где global — сквозной номер. Пересчёт старых путей `parts[4]` в сквозные номера
делается по разделам RTDB; при загрузке сверяйте номер с ответом задачи.

## Ограничения

- Настройки прокси (URL/секрет) — дефолтные SharedPreferences, чистятся только
  при полном сбросе приложения; смена секрета требует пересборки cron-подписи.
- Время телефона должно быть синхронизировано (окно ±300 с).
- Presigned-ссылка живёт 1 час; Coil грузит сразу.
