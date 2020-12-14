package com.maiya.call.phone.utils

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.*

/**
 * 前台Activity的数量，这里的前台是指:应用调用了onStart且尚未调用onStop<br></br>
 * A 启动 B 调用顺序   A.onPause() B.onCreate() B.onStart() B.onResume() A.onActivitySaveInstanceState()
 * 这个时候按返回键，调用顺序  B.onPause() -> A.onResume() ->  B.onStop() -> B.onDestroy()
 */
class GlobalActivityLifecycleMonitor : ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {}
    override fun onActivityStarted(activity: Activity) {
        mForegroundActivityCount++
        tryNotifyAppStateChange()
    }

    override fun onActivityResumed(activity: Activity) {
        if (mTopActivityRef == null || mTopActivityRef!!.get() == null || mTopActivityRef!!.get() != activity) {
            mTopActivityRef = WeakReference(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {
        mForegroundActivityCount--
        tryNotifyAppStateChange()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
    internal class SimpleRef(callback: AppLifeCallback?) {
        var ref: WeakReference<AppLifeCallback?>?
        val isOccupied: Boolean
            get() = ref != null && ref!!.get() != null

        fun update(callback: AppLifeCallback?) {
            ref = WeakReference(callback)
        }

        fun clear() {
            ref = null
        }

        fun get(): AppLifeCallback? {
            return if (ref == null) null else ref!!.get()
        }

        init {
            ref = WeakReference(callback)
        }
    }

    interface AppLifeCallback {
        fun onAppForeground()
        fun onAppBackground()
    }

    companion object {
        private val appLifeCallbackList: MutableList<SimpleRef> = LinkedList()
        private var sInstance: GlobalActivityLifecycleMonitor? = null
        private var mTopActivityRef: WeakReference<Activity?>? = null
        private var mForegroundActivityCount = 0
        private var lastIsAppForeground = false
        private var lastIsAppBackground = false
        fun register(app: Application) {
            if (sInstance == null) {
                synchronized(GlobalActivityLifecycleMonitor::class.java) {
                    if (sInstance == null) {
                        sInstance = GlobalActivityLifecycleMonitor()
                        app.registerActivityLifecycleCallbacks(sInstance)
                    }
                }
            }
        }

        val isAppOnForeground: Boolean
            get() = mForegroundActivityCount > 0

        private fun tryNotifyAppStateChange() {
            val currentIsAppForeground = isAppOnForeground
            val currentIsAppBackground = !currentIsAppForeground
            if (!lastIsAppForeground && currentIsAppForeground) {
                onAppForeground()
            }
            if (!lastIsAppBackground && currentIsAppBackground) {
                onAppBackground()
            }
            lastIsAppForeground = currentIsAppForeground
            lastIsAppBackground = currentIsAppBackground
        }

        @Synchronized
        private fun onAppForeground() {
            for (ref in appLifeCallbackList) {
                val callback = ref.get()
                callback?.onAppForeground()
            }
        }

        @Synchronized
        private fun onAppBackground() {
            for (ref in appLifeCallbackList) {
                val callback = ref.get()
                callback?.onAppBackground()
            }
        }

        //        if (ContextUtils.isDestroyed(activity)) {
//            return null;
//        }
        val topActivity: Activity?
            get() = if (mTopActivityRef == null) {
                null
            } else mTopActivityRef!!.get()
        //        if (ContextUtils.isDestroyed(activity)) {
//            return null;
//        }

        @Synchronized
        fun addAppLifeCallback(callback: AppLifeCallback?) {
            if (callback == null) {
                return
            }
            val size = appLifeCallbackList.size
            for (i in 0 until size) {
                val ref = appLifeCallbackList[i]
                if (!ref.isOccupied) {
                    ref.update(callback)
                    return
                }
            }
            appLifeCallbackList.add(SimpleRef(callback))
        }

        @Synchronized
        fun removeAppLifeCallback(callback: AppLifeCallback?) {
            if (callback == null) {
                return
            }
            val size = appLifeCallbackList.size
            for (i in 0 until size) {
                val ref = appLifeCallbackList[i]
                if (ref.get() === callback) {
                    ref.clear()
                }
            }
        }
    }
}