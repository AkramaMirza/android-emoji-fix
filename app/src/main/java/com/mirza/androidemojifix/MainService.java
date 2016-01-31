package com.mirza.androidemojifix;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends Service {
    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent i = new Intent(this, RenderDialogActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), i, 0);

        Notification.Builder nb = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Emoji Fix")
                .setContentText("")
                //TODO: add icons
                .addAction(0, "Render", pi)
                .addAction(0, "Keyboard", pi);

        Notification notification = nb.build();
        startForeground((int)System.currentTimeMillis(), notification);

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
