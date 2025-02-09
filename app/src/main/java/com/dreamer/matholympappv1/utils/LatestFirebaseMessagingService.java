package com.dreamer.matholympappv1.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.dreamer.matholympappv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class LatestFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = LatestFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Новый FCM токен: " + token);

        // Обновляем токен в Firebase Database
        updateTokenInFirebase(token);
    }

    private void updateTokenInFirebase(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("token_id");  // Поле token_id, как в вашей базе

            ref.setValue(token)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM-токен успешно обновлён"))
                    .addOnFailureListener(e -> Log.e(TAG, "Ошибка при обновлении FCM-токена: " + e.getMessage()));
        } else {
            Log.e(TAG, "Пользователь не авторизован, не удалось обновить FCM-токен");
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message received: " + remoteMessage.getData());
        // Обработка входящего сообщения (например, уведомление)
        Log.d(TAG, "Получено сообщение: " + remoteMessage.getData());

        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FCM_CHANNEL")
                .setSmallIcon(R.drawable.ic_baseline_bubble_chart_24)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}