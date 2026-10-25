"""
Cron-пингер FastAPI-прокси (держать контейнер Render живым раз в минуту).

Подпись та же, что у Android-клиента (MinioProxyImageLoader):
  X-Auth: "<unix-секунд>:<hex HMAC-SHA256(secret, ts + '\n' + body)>"
Тело пинга — пустое, эндпоинт /ping.

Запуск (cron, раз в минуту):
  * * * * * /usr/bin/python3 /path/to/cron_ping.py >> /var/log/cron_ping.log 2>&1

ENV:
  PROXY_URL    — https://<host>/ping
  PROXY_SECRET — общий секрет (MINIO_PROXY_SECRET на сервере)
"""
import hashlib
import hmac
import os
import sys
import time
import urllib.request

PROXY_URL = os.environ.get("PROXY_URL", "")
PROXY_SECRET = os.environ.get("PROXY_SECRET", "").encode()


def sign(secret: bytes, ts: str, body: bytes) -> str:
    return hmac.new(secret, ts.encode() + b"\n" + body, hashlib.sha256).hexdigest()


def main() -> int:
    if not PROXY_URL or not PROXY_SECRET:
        print("PROXY_URL/PROXY_SECRET не заданы", file=sys.stderr)
        return 2
    body = b""
    ts = str(int(time.time()))
    req = urllib.request.Request(
        PROXY_URL,
        data=body,
        method="POST",
        headers={"X-Auth": ts + ":" + sign(PROXY_SECRET, ts, body)},
    )
    try:
        with urllib.request.urlopen(req, timeout=20) as resp:
            print(time.strftime("%Y-%m-%d %H:%M:%S"), resp.status, resp.read().decode())
            return 0
    except Exception as exc:
        print(time.strftime("%Y-%m-%d %H:%M:%S"), "FAIL:", exc, file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
