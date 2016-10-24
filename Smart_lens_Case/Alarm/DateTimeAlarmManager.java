package com.yangproject.iot.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 우민 on 2016-06-04.
 */
public abstract class DateTimeAlarmManager {
    private static final String TAG = "AlarmService";

    private Context context;
    private AlarmManager mAlarmManager;

    private boolean isStarted;

    public DateTimeAlarmManager(Context context) {
        this.context = context;
        mAlarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
    }

    public abstract void startAlarmService(String param);
    public abstract void stopAlarmService();

    public void startDateTimeAlarmService(String intentFilterType, String forDateTime, String toastMessage1, String toastMessage2) {
        Intent intent = new Intent(intentFilterType);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        try {
            SimpleDateFormat todaysdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            String today = todaysdf.format(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.KOREA);
            sdf.setLenient(false);
            Date current = sdf.parse(sdf.format(new Date()));
            Date dateTime = sdf.parse(today + " " + forDateTime);
            long diff = dateTime.getTime() - current.getTime();
            if(diff < 0) {
                diff += 86400000;
            }
            try {
                mAlarmManager.cancel(pendingIntent);
            }
            catch (Exception exc) { }
            Log.d("setAlarmService", "datetime : " + forDateTime + " systemclock : " + SystemClock.elapsedRealtime() + " diff : " + diff + " datetime : " + dateTime.getTime() + " curtime : " + current.getTime() + " " + intentFilterType);
            mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + diff, pendingIntent); // 2minutes
            Toast.makeText(context, toastMessage1, Toast.LENGTH_SHORT).show();
        }
        catch (ParseException pe) {
            Log.d(TAG, toastMessage2);
            // Toast.makeText(context, toastMessage2, Toast.LENGTH_SHORT).show();
        }
        isStarted = true;
    }

    public boolean checkStarted() {
        return isStarted;
    }

    public void stopDateTimeAlarmService(String intentFilterType, String toastMessage) {
        try {
            if (mAlarmManager != null) {
                Intent intent = new Intent(intentFilterType);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                mAlarmManager.cancel(pendingIntent);
                // Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception exc) {
            Log.d(TAG, "stopAlarmService failed");
        }
        isStarted = false;
    }
}
