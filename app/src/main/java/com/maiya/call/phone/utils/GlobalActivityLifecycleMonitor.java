package com.maiya.call.phone.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;


/**
 * 前台Activity的数量，这里的前台是指:应用调用了onStart且尚未调用onStop<br/>
 * A 启动 B 调用顺序   A.onPause() B.onCreate() B.onStart() B.onResume() A.onActivitySaveInstanceState()
 * 这个时候按返回键，调用顺序  B.onPause() -> A.onResume() ->  B.onStop() -> B.onDestroy()
 */
public class GlobalActivityLifecycleMonitor implements Application.ActivityLifecycleCallbacks {


    private static final List<SimpleRef> appLifeCallbackList = new LinkedList<>();

    private static GlobalActivityLifecycleMonitor sInstance;

    private static WeakReference<Activity> mTopActivityRef;
    private static int mForegroundActivityCount;
    private static boolean lastIsAppForeground;
    private static boolean lastIsAppBackground;

    public static void register(Application app) {
        if (sInstance == null) {
            synchronized (GlobalActivityLifecycleMonitor.class) {
                if (sInstance == null) {
                    sInstance = new GlobalActivityLifecycleMonitor();
                    app.registerActivityLifecycleCallbacks(sInstance);
                }
            }
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mForegroundActivityCount++;
        tryNotifyAppStateChange();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (mTopActivityRef == null
                || mTopActivityRef.get() == null
                || !mTopActivityRef.get().equals(activity)) {
            mTopActivityRef = new WeakReference(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mForegroundActivityCount--;
        tryNotifyAppStateChange();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    public static boolean isAppOnForeground() {
        return mForegroundActivityCount > 0;
    }

    private static void tryNotifyAppStateChange() {
        boolean currentIsAppForeground = isAppOnForeground();
        boolean currentIsAppBackground = !currentIsAppForeground;
        if (!lastIsAppForeground && currentIsAppForeground) {
            onAppForeground();
        }
        if (!lastIsAppBackground && currentIsAppBackground) {
            onAppBackground();
        }
        lastIsAppForeground = currentIsAppForeground;
        lastIsAppBackground = currentIsAppBackground;
    }


    private static synchronized void onAppForeground() {
        for (SimpleRef ref : appLifeCallbackList) {
            AppLifeCallback callback = ref.get();
            if (callback != null) {
                callback.onAppForeground();
            }
        }
    }

    private static synchronized void onAppBackground() {
        for (SimpleRef ref : appLifeCallbackList) {
            AppLifeCallback callback = ref.get();
            if (callback != null) {
                callback.onAppBackground();
            }
        }
    }

    public static Activity getTopActivity() {
        if (mTopActivityRef==null) {
            return null;
        }
        //        if (ContextUtils.isDestroyed(activity)) {
//            return null;
//        }
        return mTopActivityRef.get();
    }

    public static synchronized void addAppLifeCallback(AppLifeCallback callback) {
        if (callback == null) {
            return;
        }
        int size = appLifeCallbackList.size();
        for (int i = 0; i < size; i++) {
            SimpleRef ref = appLifeCallbackList.get(i);
            if (!ref.isOccupied()) {
                ref.update(callback);
                return;
            }
        }
        appLifeCallbackList.add(new SimpleRef(callback));
    }

    public static synchronized void removeAppLifeCallback(AppLifeCallback callback) {
        if (callback == null) {
            return;
        }
        int size = appLifeCallbackList.size();
        for (int i = 0; i < size; i++) {
            SimpleRef ref = appLifeCallbackList.get(i);
            if (ref.get() == callback) {
                ref.clear();
            }
        }
    }

    static class SimpleRef {
        WeakReference<AppLifeCallback> ref;

        SimpleRef(AppLifeCallback callback) {
            ref = new WeakReference<>(callback);
        }

        boolean isOccupied() {
            return ref != null && ref.get() != null;
        }

        void update(AppLifeCallback callback) {
            ref = new WeakReference<>(callback);
        }

        void clear() {
            ref = null;
        }

        AppLifeCallback get() {
            return ref == null ? null : ref.get();
        }
    }

    public interface AppLifeCallback {
        void onAppForeground();

        void onAppBackground();
    }
}

