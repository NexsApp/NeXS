package in.co.nexs.nexsapp.services;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import in.co.nexs.nexsapp.App;
import in.co.nexs.nexsapp.R;
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
                .setSmallIcon(R.drawable.nexs)                  //Proper drawable required
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