package com.maiya.call

import android.app.Application
import com.maiya.call.phone.manager.CallerShowManager
import com.maiya.call.phone.utils.GlobalActivityLifecycleMonitor

/**
 * Author : ymc
 * Date   : 2020/5/29  23:23
 * Class  : MyApplication
 */

class MApplication : Application() {
    private lateinit var myApplication: MApplication

    override fun onCreate() {
        super.onCreate()
        myApplication = this
        /**
         * 生命周期监控
         */
        GlobalActivityLifecycleMonitor.register(this)
        //初始化Call
        CallerShowManager.instance.initCallerShow(this)
    }

    @Synchronized
    fun getInstance(): MApplication {
        return myApplication
    }
}