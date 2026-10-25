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

Cron: `proxy/cron_ping.py` (ENV PROXY_URL, PROXY_SECRET; подпись `ts + "\n" + пустое
тело`, POST `/ping`), crontab:

```
* * * * * /usr/bin/python3 /path/to/proxy/cron_ping.py >> /var/log/cron_ping.log 2>&1
```

## Загрузка в MinIO (скрипт мигрирует старые картинки)

`proxy/upload_images.py --export export.json --dir ./images [--dry-run]`:
JSON-выгрузка каталога `Zadachi` из Firebase Console + локальные картинки по
схеме `<dir>/<section_key>/answer_N.jpg` / `solution_N.jpg`. Сквозной номер
считается по той же формуле, что приложение (цифра в конце ключа раздела,
offset по разделам с меньшим номером), номера совпадут. Объекты:

```
tasks/<global>/answer.jpg
tasks/<global>/solution.jpg
```

Совпадение подписи клиент/сервер закреплено юнит-тестом
`MinioProxyHmacTest` (векторы из `proxy/gen_vectors.py`).

## Ограничения

- Настройки прокси (URL/секрет) — дефолтные SharedPreferences, чистятся только
  при полном сбросе приложения; смена секрета требует пересборки cron-подписи.
- Время телефона должно быть синхронизировано (окно ±300 с).
- Presigned-ссылка живёт 1 час; Coil грузит сразу.
