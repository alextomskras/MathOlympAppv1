package com.dreamer.matholympappv1.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Проверка, что клиентская подпись (hmacHex) совпадает с проверкой FastAPI-прокси
 * (sign в proxy/fastapi_minio_proxy.py). Векторы сгенерированы python-скриптом
 * proxy/gen_vectors.py — если обе реализации схемы "ts + \n + body" совпадают,
 * подписанные запросы приложения прокси принимает.
 */
public class MinioProxyHmacTest {

    @Test
    public void hmacMatchesProxyVectors() {
        String secret = "secret123";
        String body = "1700000000\n{\"global\":42,\"kind\":\"answer\"}";
        assertEquals("7fa92d36c1537c2607aa0cc461ef3ab5ef56355693a1fd725f3af6c70dac4661",
                MinioProxyImageLoader.hmacHex(secret, body));
        assertEquals("8cd8d1e621e159b08b16e1cce6e397f912d3e7680a2a4e0c85217131f7ef0101",
                MinioProxyImageLoader.hmacHex(secret, "123\n"));
    }
}
