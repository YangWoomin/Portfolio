package com.yang.yangproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 우민 on 2016-08-12.
 */
public class ProximityService extends Service implements SensorEventListener {
    private SensorManager sm;
    private Sensor prox;
    private boolean checkMotion;
    private Timer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("test", "Service started!");

        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        prox = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener(this, prox, SensorManager.SENSOR_DELAY_NORMAL);
        checkMotion = false;

        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if(event.values[0] < event.sensor.getMaximumRange()) {
                /*if(checkMotion) {
                    UnityPlayer.UnitySendMessage("Field1GameManager", "setZoom", "");
                    resetTimer();
                }
                else {
                    checkMotion = true;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            resetTimer();
                        }
                    }, 1000);
                }*/
                UnityPlayer.UnitySendMessage("Field1GameManager", "setZoom", "");
            }
        }
    }

    public void resetTimer() {
        try {
            checkMotion = false;
            timer.cancel();
        }
        catch (Exception exc) { }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {

        Log.d("test", "Service stopped!");

        try {
            sm.unregisterListener(this);
        }
        catch (Exception exc) { }

        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
