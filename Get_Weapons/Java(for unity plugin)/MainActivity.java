package com.yang.yangproject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Vibrator;

import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {
    private Intent proxIntent;
    private Vibrator vibrator;
    private long[] pattern = {50, 50, 50, 50, 50, 50};

    public int StartProximityService() {
        try {
            if(checkService("ProximityService")) {
                stopService(proxIntent);
            }
            proxIntent = new Intent(this, ProximityService.class);
            startService(proxIntent);
            return 0;
        }
        catch (Exception exc) {
            return -1;
        }
    }

    public int StopProximityService() {
        try {
            stopService(proxIntent);
            return 0;
        }
        catch (Exception exc) {
            return -1;
        }
    }
    public int doVibrate() {
        try {
            vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, -1);
            return 0;
        }
        catch (Exception exc) {
            return -1;
        }
    }

    public boolean checkService(String serviceName) {
        ActivityManager am = (ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
