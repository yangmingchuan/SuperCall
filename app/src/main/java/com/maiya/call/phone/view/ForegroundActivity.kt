package com.maiya.call.phone.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.FragmentActivity
import com.maiya.call.phone.service.CallListenerService
import com.maiya.call.phone.service.TaskServiceManager.bindStepService

/**
 * 适配 android 6.0 电话挂断
 * 将app拉到前台
 */
class ForegroundActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        next(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        next(intent)
    }

    private fun next(intent: Intent?) {
        if (intent == null) {
            delayFinishSelf()
            return
        }
        try {
            intent.action = CallListenerService.ACTION_PHONE_CALL
            intent.setClass(applicationContext, CallListenerService::class.java)
            bindStepService(intent)
            delayFinishSelf()
        } catch (e: Exception) {
        }
    }

    private fun delayFinishSelf() {
        Handler().postDelayed({ finish() }, 500)
    }
}