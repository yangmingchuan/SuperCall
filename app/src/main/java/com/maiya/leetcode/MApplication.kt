package com.maiya.leetcode

import android.app.Application
import android.content.Context
import com.maiya.leetcode.phone.manager.CallerShowManager
import com.maiya.leetcode.phone.utils.GlobalActivityLifecycleMonitor

/**
 * Author : ymc
 * Date   : 2020/5/29  23:23
 * Class  : MyApplication
 */

class MApplication : Application() {

    private val sContext = this

     fun getContext() : Context {
        return sContext
    }

    /**
     * 延迟 单例 初始化
     */
    companion object {
        val instance : MApplication by lazy {
            MApplication()
        }
    }

    override fun onCreate() {
        super.onCreate()
        /**
         * 生命周期监控
         */
        GlobalActivityLifecycleMonitor.register(this)
        //初始化Call
        CallerShowManager.instance.initCallerShow(this)
    }


}