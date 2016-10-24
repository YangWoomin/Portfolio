package com.yangproject.iot.Others;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by 우민 on 2016-06-03.
 */
public class PushWakeLock {
    private static PowerManager.WakeLock sCpuWakeLock;

    static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "hello");
        sCpuWakeLock.acquire();
    }

    static void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}