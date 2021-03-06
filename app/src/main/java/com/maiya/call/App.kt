package com.maiya.call

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.multidex.MultiDex
import com.maiya.call.phone.manager.CallerShowManager
import com.maiya.call.phone.receiver.PhoneStateReceiver
import kotlin.properties.Delegates

/**
 * Author : ymc
 * Date   : 2020/5/29  23:23
 * Class  : MyApplication
 */

class App : Application() {

    companion object {
        val TAG = "superCall"

        var context: Context by Delegates.notNull()
            private set

        lateinit var instance: Application

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        //initCallerShow(this)
    }

    private fun initCallerShow(application: Application) {
        CallerShowManager.instance.initCallerShow(application)
        val intentFilter = IntentFilter()
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL)
        application.applicationContext.registerReceiver(PhoneStateReceiver(), intentFilter)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    private val mActivityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.e(TAG, "onCreated: " + activity.componentName.className)
        }

        override fun onActivityStarted(activity: Activity) {
            Log.e(TAG, "onStart: " + activity.componentName.className)
        }

        override fun onActivityResumed(activity: Activity) {
            Log.e(TAG, "onResumed: " + activity.componentName.className)
        }

        override fun onActivityPaused(activity: Activity) {
            Log.e(TAG, "onPaused: " + activity.componentName.className)
        }

        override fun onActivityStopped(activity: Activity) {
            Log.e(TAG, "onStopped: " + activity.componentName.className)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "onDestroy: " + activity.componentName.className)
        }
    }


}