package com.yangproject.iot.Others;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.yangproject.iot.Core.MainActivity;
import com.yangproject.iot.R;

import java.util.Random;

/**
 * Created by 우민 on 2016-06-03.
 */
public class MyNotification {
    private Context context;
    public MyNotification(Context context) {
        this.context = context;
    }

    public void notification(String preview, String title, String content) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,  new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker(preview);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(android.app.Notification.PRIORITY_MAX);
        NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Random generator = new Random();
        nm.notify(generator.nextInt(999999) + 1, builder.build());
        acquireWakeLock();
    }

    private void acquireWakeLock() {
        try {
            PushWakeLock.acquireCpuWakeLock(context);
            PushWakeLock.releaseCpuLock();
        }
        catch (Exception exc) { }
    }
}
