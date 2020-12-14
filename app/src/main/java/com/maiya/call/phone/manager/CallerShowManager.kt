package com.maiya.call.phone.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.maiya.call.phone.service.CallListenerService
import com.maiya.call.phone.service.TaskServiceManager

/**
 * 电话显示管理
 */

class CallerShowManager private constructor() {

    companion object {
        val instance: CallerShowManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CallerShowManager()
        }
    }

    fun initCallerShow(context: Context?) {
        try {
            context?.let {
                TaskServiceManager.bindStepService(Intent(it, CallListenerService::class.java))
                FloatingWindowManager.instance.initManager(it)
            }
        } catch (e: Exception) {
        }
    }

    fun setRingShow(activity: Activity, listener: OnPerManagerListener?) {
        CallerShowPermissionManager.instance.checkAndRequestPhonePermission(activity, object : CallerShowPermissionManager.CallBack {
            override fun onFailed() {
                listener?.onDenied()
            }

            override fun onSuccess() {
                listener?.onGranted()
            }
        })
    }

    interface OnPerManagerListener {

        fun onGranted()

        fun onDenied()

    }

}