package com.mirza.androidemojifix;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.os.Environment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import java.io.File;

import de.greenrobot.event.EventBus;

public class MainService extends InputMethodService {
    private Drawable[] drawables;
    private String[] namesWithExtension;
    private BubblesManager bubblesManager;

    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        Intent renderIntent = new Intent(this, RenderDialogActivity.class);
        PendingIntent renderPi = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), renderIntent, 0);

        Intent keyboardIntent = new Intent(this, KeyboardDialogActivity.class);
        PendingIntent keyboardPi = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), keyboardIntent, 0);

        Notification.Builder nb = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Emoji Fix")
                .setContentText("")
                //TODO: add icons
                .addAction(0, "Render", renderPi)
                .addAction(0, "Keyboard", keyboardPi);

        Notification notification = nb.build();
        startForeground((int)System.currentTimeMillis(), notification);

    }

    public void onEvent(RequestDrawablesEvent e) {
        EventBus.getDefault().post(new GetDrawablesEvent(drawables, namesWithExtension));
    }

    public void onEvent(AddBubbleEvent e) {
        bubblesManager.addBubble(e.getBubbleLayout(), 0, 500);
    }

    public void onEvent(RemoveBubbleEvent e) {
        bubblesManager.removeBubble(e.getBubbleLayout());
    }

    public void onEvent(TypeCharacterEvent e) {
        String input = e.getInput();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentInputConnection() != null) {
            getCurrentInputConnection().commitText(input, 1);
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        bubblesManager.recycle();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                File.separator + "AndroidEmojiFix" + File.separator);
        File[] files = mediaDirectory.listFiles();
        namesWithExtension = mediaDirectory.list();
        drawables = new Drawable[files.length];
        for (int i = 0; i < files.length; i++) {
            drawables[i] = Drawable.createFromPath(files[i].getAbsolutePath());
        }

        bubblesManager = new BubblesManager.Builder(this).setTrashLayout(R.layout.bubble_trash_layout).build();
        bubblesManager.initialize();
        return START_STICKY;
    }
}
