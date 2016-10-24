package com.yangproject.iot.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.yangproject.iot.Data.LensData;
import com.yangproject.iot.Others.Constants;
import com.yangproject.iot.Others.MyNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 우민 on 2016-06-04.
 */
public class ExpirationDateAlarmManager extends DateTimeAlarmManager {
    private static final String TAG = "ExpirationDateAS";

    private Context context;

    private MyNotification mMyNotification;
    private LensData mData;

    public ExpirationDateAlarmManager(Context context) {
        super(context);
        this.context = context;

        mMyNotification = new MyNotification(this.context);
        mData = new LensData(this.context);

        IntentFilter filter = new IntentFilter(Constants.EXPIRATION_INTENT_FILTER);
        this.context.registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.EXPIRATION_INTENT_FILTER)) {
                startAlarmService(null);
            }
        }
    };

    public void doService(String columnName, String simpleDataFormat, long gap, String notiTitle, String notiContent) {
        for(int i = 1; i < Constants.LENS_MAX_NUMBER + 1; i++) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(simpleDataFormat,  Locale.KOREA);
                sdf.setLenient(false);
                Date date = sdf.parse(mData.getData(Constants.LENS + i, columnName));
                Date current = sdf.parse(sdf.format(new Date()));
                long diff = date.getTime() - current.getTime();
                if(diff <= gap) {
                    String name = mData.getData(Constants.LENS + i, Constants.LENS_NAME);
                    mMyNotification.notification(notiTitle + " " + name + " " + i + " lens", notiContent, notiTitle + " " + name + " " + i + " lens");
                    if(gap == -Constants.WASH_PERIOD) {
                        commandWashing(i);
                    }
                }
            }
            catch (Exception exc) {
                Log.d(TAG, "doService failed");
            }
        }
    }

    public void commandWashing(int lensNum) {
        Intent intent = new Intent(Constants.COMMAND_WASHING_INTENT_FILTER);
        intent.putExtra("lens_number", lensNum);
        context.sendBroadcast(intent);
    }

    @Override
    public void startAlarmService(String param) {
        doService(Constants.LENS_EXPIRATION_DATE, "yyyy-MM-dd", 0, "Expiration date of ", "Expiration date alarm!!");
        doService(Constants.LENS_LAST_WASH_DATE, "yyyy-MM-dd HH:mm:ss", -Constants.WASH_PERIOD, "Washed your ", "Wash time alarm!!");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        sdf.setLenient(false);

        long forDemoIn1min = new Date().getTime() + 120000; // 2minutes
        String forDemoIn1minStr = sdf.format(forDemoIn1min);
        super.startDateTimeAlarmService(Constants.EXPIRATION_INTENT_FILTER, forDemoIn1minStr, "Expiration date alarm started", "Expiration date alarm failed");

        /*String current1s = sdf.format(new Date());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) { }
        super.startDateTimeAlarmService(Constants.EXPIRATION_INTENT_FILTER, current1s, "Expiration date alarm started", "Expiration date alarm failed");*/
    }

    @Override
    public void stopAlarmService() {
        super.stopDateTimeAlarmService(Constants.EXPIRATION_INTENT_FILTER, "Expiration date alarm stopped");
    }
}
