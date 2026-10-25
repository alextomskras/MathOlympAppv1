package com.dreamer.matholympappv1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.preference.PreferenceManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import coil.Coil;
import coil.request.ImageRequest;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Клиент токен-подписного HTTP-прокси (FastAPI на Render), который отдаёт
 * presigned GET-ссылку на картинку задачи из MinIO по сквозному номеру.
 *
 * Контракт (см. proxy/fastapi_minio_proxy.py и README-MINIO.md):
 *   POST {base_url}, заголовок X-Auth: "<unix-секунд>:<hex HMAC-SHA256(secret, ts + \n + body)>"
 *   тело: {"global": N, "kind": "answer" | "solution"}
 *   ответ: {"url": "<presigned GET>", ...}; неподписанный POST прокси не принимает.
 *
 * Конфигурация: SettingsFragment (дефолтные SharedPreferences) —
 * remote_proxy_enabled, remote_proxy_base_url, remote_proxy_secret.
 * По умолчанию выключено: картинка грузится из Firebase Storage.
 */
public final class MinioProxyImageLoader {

    private static final String TAG = "MinioProxyLoader";
    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8");

    public static final String KIND_ANSWER = "answer";
    public static final String KIND_SOLUTION = "solution";

    private static OkHttpClient client;

    private MinioProxyImageLoader() {
    }

    /** Конфигурация: включено + непустые URL и секрет в дефолтных SharedPreferences. */
    public static boolean isConfigured(Context context) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        return p.getBoolean(PrefKeys.REMOTE_PROXY_ENABLED, false)
                && !p.getString(PrefKeys.REMOTE_PROXY_BASE_URL, "").isEmpty()
                && !p.getString(PrefKeys.REMOTE_PROXY_SECRET, "").isEmpty();
    }

    /**
     * Запросить картинку по сквозному номеру. Синает запрос в фоновом потоке,
     * ответ грузит в ImageView через Coil на главном потоке.
     */
    public static void loadImage(int globalNumber, String kind, ImageView target, Context context) {
        final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        final String baseUrl = p.getString(PrefKeys.REMOTE_PROXY_BASE_URL, "");
        final String secret = p.getString(PrefKeys.REMOTE_PROXY_SECRET, "");
        if (baseUrl.isEmpty() || secret.isEmpty()) {
            Log.w(TAG, "Прокси не настроен: пустой URL или секрет");
            return;
        }
        final Context appContext = context.getApplicationContext();

        new Thread(() -> {
            try {
                String ts = Long.toString(System.currentTimeMillis() / 1000L);
                String body = "{\"global\":" + globalNumber + ",\"kind\":\"" + kind + "\"}";
                String sig = hmacHex(secret, ts + "\n" + body);

                Request request = new Request.Builder()
                        .url(baseUrl)
                        .header("X-Auth", ts + ":" + sig)
                        .post(RequestBody.create(JSON_MEDIA, body))
                        .build();

                try (Response resp = getOkClient().newCall(request).execute()) {
                    String json = resp.body() != null ? resp.body().string() : "";
                    if (!resp.isSuccessful()) {
                        Log.e(TAG, "Прокси вернул HTTP " + resp.code() + ": " + json);
                        return;
                    }
                    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                    String url = obj.has("url") ? obj.get("url").getAsString() : null;
                    if (url == null || url.isEmpty()) {
                        Log.e(TAG, "Прокси не вернул url (global=" + globalNumber + ")");
                        return;
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        ImageRequest req = new ImageRequest.Builder(appContext)
                                .data(url)
                                .target(target)
                                .build();
                        Coil.imageLoader(appContext).enqueue(req);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка запроса к прокси (global=" + globalNumber + ")", e);
            }
        }).start();
    }

    /** HEX(HMAC-SHA256(secret, data)) — та же схема, что проверяет FastAPI-прокси. */
    public static String hmacHex(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA256 недоступен", e);
        }
    }

    private static OkHttpClient getOkClient() {
        if (client == null) {
            synchronized (MinioProxyImageLoader.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return client;
    }
}
