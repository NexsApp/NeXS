package com.example.nexs.services;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nexs.App;
import com.example.nexs.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmCustomService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        createNotification(remoteMessage);
    }

    private void createNotification(RemoteMessage remoteMessage) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.googleg_disabled_color_18);
        Notification notification = new NotificationCompat
                .Builder(this, App.NEWS)
                .setSmallIcon(R.drawable.ic_baseline_login_24)  //Proper drawable required
                .setLargeIcon(bitmap)                           //Proper icon required
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(remoteMessage.getData().get("data"))
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, notification);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.i("FCM", s);
    }
}