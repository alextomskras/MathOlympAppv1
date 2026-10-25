import hmac, hashlib
s = b"secret123"
d1 = '1700000000\n{"global":42,"kind":"answer"}'
d2 = "123\n"
print(hmac.new(s, d1.encode(), hashlib.sha256).hexdigest())
print(hmac.new(s, d2.encode(), hashlib.sha256).hexdigest())
# проверка самописной схемы: серверная sign == клиентская подпись
ts, body = "1700000000", b'{"global":42,"kind":"answer"}'
srv = hmac.new(s, ts.encode() + b"\n" + body, hashlib.sha256).hexdigest()
print(srv)
