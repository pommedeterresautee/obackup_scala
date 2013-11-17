/*
 * Copyright (c) 2013
 * This file is part of 2BOrange Android application and is the property of its Author, Michaël BENESTY.
 *
 * You may be sued by the Author for any violation of this property.
 *
 * Using this file in any way without an explicit authorization of the Author is a violation of this property.
 */

package com.pommedeterresautee.twoborange3;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.WindowManager;

/**
 * Manage the wake up of the device.
 */
public class SleepManager {
    private static WakeLock lockStatic = null;
    private static WifiManager.WifiLock wifilock = null;
    private static Context mContext;
    private static int mCounter = 0;

    /**
     * Put device in On state.  .
     * Check if the manager has been already started
     */
    public static void start(Context context) {
        mCounter++;
        mContext = context.getApplicationContext();
        if (!isActivatedSleep()) getWakeLock().acquire();
        if (getWifiLock() != null && !isActivatedWifi()) getWifiLock().acquire();
    }

    /**
     * Let the device fall in sleep.
     */
    public static void stop() {
        --mCounter;
        if (mContext == null || mCounter > 0) return;
        if (isActivatedSleep()) getWakeLock().release();
        if (isActivatedWifi()) getWifiLock().release();
    }

    /**
     * Keep the screen ON
     *
     * @param context
     */
    public static void keepScreenON(Activity context) {
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Switch screen Off
     *
     * @param context
     */
    public static void shutOffScreen(Activity context) {
        WindowManager.LayoutParams params = context.getWindow().getAttributes();
        params.screenBrightness = -1;
        context.getWindow().setAttributes(params);
    }

    private static boolean isActivatedWifi() {
        return mContext != null && getWifiLock() != null && getWifiLock().isHeld();
    }

    private static boolean isActivatedSleep() {
        return mContext != null && getWakeLock().isHeld();
    }

    synchronized private static WakeLock getWakeLock() {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);

            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "2BOWakeLock");
            lockStatic.setReferenceCounted(true);
        }

        return (lockStatic);
    }

    synchronized private static WifiManager.WifiLock getWifiLock() {
        if (wifilock == null) {

            ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State wifi = conMgr.getNetworkInfo(1).getState();

            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                wifilock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "WifiLock");
            } else {
                wifilock = null;
            }
        }

        return (wifilock);
    }


}

