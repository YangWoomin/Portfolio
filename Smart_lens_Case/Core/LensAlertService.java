package com.yangproject.iot.Core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.yangproject.iot.Alarm.DateTimeAlarmManager;
import com.yangproject.iot.Alarm.ExpirationDateAlarmManager;
import com.yangproject.iot.Alarm.TimeAlarmManager;
import com.yangproject.iot.Data.LensData;
import com.yangproject.iot.Mqtt.MqttManager;
import com.yangproject.iot.Others.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 우민 on 2016-06-03.
 */
public class LensAlertService extends Service {
    private static final String TAG = "LensAlertService";

    private int lensNum;
    private String sleepTime;
    private int totalLensState;
    private LensData mData;
    private MqttManager mMqttManager;
    private DateTimeAlarmManager[] mDateTimeAlarmManager = new DateTimeAlarmManager[Constants.ALARM_SERVICE_NUMBER];
    private DateTimeAlarmManager[] mRecommendAlarmManager = new DateTimeAlarmManager[Constants.LENS_MAX_NUMBER];

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "LensAlert service started", Toast.LENGTH_SHORT).show();

        IntentFilter filter = new IntentFilter();
        // filter.addAction(Constants.RENEW_SLEEP_TIME_INTENT_FILTER);
        // filter.addAction(Constants.RENEW_DATABASE_TO_SERVICE);
        filter.addAction(Constants.MQTT_TOPIC_1);
        filter.addAction(Constants.MQTT_TOPIC_2);
        filter.addAction(Constants.COMMAND_WASHING_INTENT_FILTER);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lensNum = intent.getIntExtra("lens_number", 0);
        totalLensState = 0;
        mMqttManager = new MqttManager(this, lensNum);
        mData = new LensData(this);

        try {
            sleepTime = mData.getData(Constants.SLEEP_TIME, Constants.SLEEP_TIME) + ":00";
            mDateTimeAlarmManager[0] = new TimeAlarmManager(this, sleepTime, Constants.SLEEP_TIME_INTENT_FILTER,
                    "Remove lens from your eyes for sleep!", "Sleep time alarm!!", true);
            mDateTimeAlarmManager[1] = new ExpirationDateAlarmManager(this);
            if(lensNum > 0) {
                mDateTimeAlarmManager[1].startAlarmService(null);
            }
        }
        catch (Exception exc) {
            Log.d(TAG, "onStartCommand failed");
        }
        mMqttManager.tryMqttConnection();
        return super.onStartCommand(intent, flags, startId);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*if(intent.getAction().equals(Constants.RENEW_SLEEP_TIME_INTENT_FILTER)) {
                Log.d("broadcast_receiver", "renew_sleep_time");
                sleepTime = intent.getStringExtra("sleep_time") + ":00";
                lensNum = intent.getIntExtra("lens_num", 0);
                mDateTimeAlarmManager[0].stopAlarmService();
                if(lensNum > 0) {
                    mDateTimeAlarmManager[0].startAlarmService(sleepTime);
                }
            }
            else if(intent.getAction().equals(Constants.RENEW_DATABASE_TO_SERVICE)) {
                lensNum = intent.getIntExtra("lens_num", 0);
                mDateTimeAlarmManager[1].stopAlarmService();
                if(lensNum > 0) {
                    mDateTimeAlarmManager[1].startAlarmService(null);
                }
            }
            else */if(intent.getAction().equals(Constants.MQTT_TOPIC_1)) {
                int lensNum = intent.getIntExtra("lens_number", 0);
                String lensName;
                try {
                    lensName = convertMessageToLensName(lensNum);
                    if(lensName.length() > 0) {
                        renewLastWashDateTime(lensNum);
                        Toast.makeText(getApplicationContext(), "Last washing date updated : " + lensName + " lens", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception exc) {
                    Log.d(TAG, Constants.MQTT_TOPIC_1 + " failed");
                }
            }
            else if(intent.getAction().equals(Constants.MQTT_TOPIC_2)) {
                try {
                    int lensNum = Integer.valueOf(intent.getStringExtra("lens_number")) - 1;
                    String lensState = intent.getStringExtra("lens_state");
                    Log.d(TAG, lensNum + " " + lensState);
                    if(lensState.equals("in")) {
                        try {
                            mRecommendAlarmManager[lensNum].stopAlarmService();
                        } catch (Exception exc2) { }
                        Toast.makeText(getApplicationContext(), "Recommend alarm Stopped - lens " + (lensNum + 1), Toast.LENGTH_SHORT).show();
                        totalLensState--;
                        if(totalLensState < 1) {
                            if(mDateTimeAlarmManager[0].checkStarted()) {
                                mDateTimeAlarmManager[0].stopAlarmService();
                            }
                        }
                    }
                    else if(lensState.equals("out")) {
                        if (!checkReadyForAlarm(lensNum + 1)) {
                            Log.d(TAG, "check ready for alarm failed");
                            return;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        sdf.setLenient(false);
                        long delay = (new Date()).getTime() + Constants.RECOMMENDED_PERIOD;
                        try {
                            mRecommendAlarmManager[lensNum].stopAlarmService();
                            mRecommendAlarmManager[lensNum] = null;
                        } catch (Exception exc2) {
                        }
                        mRecommendAlarmManager[lensNum] = new TimeAlarmManager(getApplicationContext(), sdf.format(delay), Constants.RECOMMENDED_TIME_INTENT_FILTER,
                                "Recommend time over! - lens " + (lensNum + 1), "Recommend time alarm! - lens " + (lensNum + 1), false);
                        mRecommendAlarmManager[lensNum].startAlarmService(sdf.format(delay));
                        Toast.makeText(getApplicationContext(), "Recommend alarm started - lens " + (lensNum + 1), Toast.LENGTH_SHORT).show();
                        totalLensState++;
                        if (!mDateTimeAlarmManager[0].checkStarted()) {
                            mDateTimeAlarmManager[0].startAlarmService(sleepTime);
                        }
                    }
                }
                catch (Exception exc) {
                    Log.d(TAG, Constants.MQTT_TOPIC_2 +" failed");
                }
            }
            else if(intent.getAction().equals(Constants.COMMAND_WASHING_INTENT_FILTER)) {
                try {
                    int lensNum = intent.getIntExtra("lens_number", 0);
                    if(lensNum > 0) {
                        mMqttManager.commandWashing(lensNum);
                    }
                } catch (Exception exc) {
                    Log.d(TAG, Constants.COMMAND_WASHING_INTENT_FILTER);
                }
            }
        }
    };

    public boolean checkReadyForAlarm(int gotLensNum) {
        switch(gotLensNum) {
            case 1 :
                switch (lensNum) {
                    case 0 :
                        return false;
                    case 1 :
                        return true;
                    case 2 :
                        return false;
                    case 3 :
                        return true;
                    case 4 :
                        return false;
                    case 5 :
                        return true;
                    case 6 :
                        return false;
                    case 7 :
                        return true;
                    default:
                        return false;
                }
            case 2 :
                switch (lensNum) {
                    case 0 :
                        return false;
                    case 1 :
                        return false;
                    case 2 :
                        return true;
                    case 3 :
                        return true;
                    case 4 :
                        return false;
                    case 5 :
                        return false;
                    case 6 :
                        return true;
                    case 7 :
                        return true;
                    default:
                        return false;
                }
            case 3 :
                switch (lensNum) {
                    case 0 :
                        return false;
                    case 1 :
                        return false;
                    case 2 :
                        return false;
                    case 3 :
                        return false;
                    case 4 :
                        return true;
                    case 5 :
                        return true;
                    case 6 :
                        return true;
                    case 7 :
                        return true;
                    default:
                        return false;
                }
        }
        return false;
    }

    public String convertMessageToLensName(int lensNum) {
        try {
            return mData.getData(Constants.LENS + lensNum, Constants.LENS_NAME);
        }
        catch (Exception exc) {
            Log.d(TAG, "converMessageToLensName failed");
        }
        return null;
    }

    public void renewLastWashDateTime(int lensNum) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            mData.delData(Constants.LENS + lensNum, Constants.LENS_LAST_WASH_DATE);
            mData.putData(Constants.LENS + lensNum, Constants.LENS_LAST_WASH_DATE, sdf.format(new Date()));
            Intent sendingIntent = new Intent(Constants.RENEW_DATABASE_TO_ACTIVITY);
            sendBroadcast(sendingIntent);
        }
        catch (Exception exc) {
            Log.d(TAG, "renewLastWashDateTime failed");
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(mReceiver);
        }
        catch (Exception exc) { }
        for(int i = 0; i < Constants.ALARM_SERVICE_NUMBER; i++) {
            try {
                mDateTimeAlarmManager[i].stopAlarmService();
            }
            catch (Exception exc) { }
        }
        for(int i = 0; i < Constants.LENS_MAX_NUMBER; i++) {
            try {
                mRecommendAlarmManager[i].stopAlarmService();
            } catch (Exception exc) { }
        }
        mMqttManager.tryMqttClose();
        Toast.makeText(getApplicationContext(), "Canceled all alarm services", Toast.LENGTH_SHORT).show();
    }
}