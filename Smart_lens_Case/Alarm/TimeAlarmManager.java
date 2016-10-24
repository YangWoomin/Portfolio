package com.yangproject.iot.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.yangproject.iot.Others.MyNotification;

/**
 * Created by 우민 on 2016-06-04.
 */
public class TimeAlarmManager extends DateTimeAlarmManager {
    private static final String TAG = "SleepTimeAlarmManager";

    private Context context;
    private MyNotification mMyNotification;

    private String time;
    private String type;
    private String title;
    private String content;
    private boolean repeat;

    private int count;


    public TimeAlarmManager(Context context, String time, String type, String title, String content, boolean repeat) {
        super(context);
        this.context = context;
        this.time = time;
        this.type = type;
        this.title = title;
        this.content = content;
        this.repeat = repeat;

        mMyNotification = new MyNotification(this.context);

        IntentFilter filter = new IntentFilter(type);
        this.context.registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(type)) {
                count++;
                if(count > 1) {
                    return;
                }
                Log.d(TAG, type);
                mMyNotification.notification(content, title, content);
                Toast.makeText(context, type, Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) { }

                if(repeat) {
                    startAlarmService(time);
                }
            }
        }
    };



    @Override
    public void startAlarmService(String param) {
        this.time = param;
        count = 0;
        super.startDateTimeAlarmService(type, this.time,
                "Time alarm started",
                "Time alarm failed");
    }

    @Override
    public void stopAlarmService() {
        count = 0;
        super.stopDateTimeAlarmService(type, "Time alarm stopped");
    }
}
