package com.dreamer.matholympappv1.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class LatestFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = LatestFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New token received: " + token);
        // Здесь можно обновить токен на сервере или в Firebase Realtime Database
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message received: " + remoteMessage.getData());
        // Обработка входящего сообщения (например, уведомление)
    }
}