package com.maiya.call.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.maiya.call.phone.service.CallListenerService
import com.maiya.call.phone.service.TaskServiceManager

/**
 * 开机启动
 *
 * Author : ymc
 * Date   : 2020/4/29  15:52
 * Class  : AutoStartReceiver
 */

class AutoStartReceiver : BroadcastReceiver() {

    companion object {
        const val AUTO_START_RECEIVER = "jenly.autostart_action"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (AUTO_START_RECEIVER == action) {
            context?.let {
                TaskServiceManager.bindStepService(Intent(it, CallListenerService::class.java))
            }
        }
    }

}