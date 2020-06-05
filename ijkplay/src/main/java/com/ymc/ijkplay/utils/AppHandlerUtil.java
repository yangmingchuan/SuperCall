package com.ymc.ijkplay.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class AppHandlerUtil {

    private final static HandlerThread BACKGROUND_HANDLER_THREAD = new HandlerThread("app_background_thread");

    private final static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Handler backgroundHandler;


    public static Handler getMainHandler() {
        return mainHandler;
    }

    public static Handler getBackgroundHandler() {
        if (backgroundHandler == null) {
            synchronized (AppHandlerUtil.class) {
                if (backgroundHandler == null) {
                    BACKGROUND_HANDLER_THREAD.start();
                    backgroundHandler = new Handler(BACKGROUND_HANDLER_THREAD.getLooper());
                }
            }
        }
        return backgroundHandler;
    }

    public static void runInUiThread(Runnable runnable) {
        getMainHandler().post(runnable);
    }

    public static void runInBackgroundThread(Runnable runnable) {
        getBackgroundHandler().post(runnable);
    }
}

