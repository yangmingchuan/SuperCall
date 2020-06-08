package com.maiya.leetcode.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.maiya.leetcode.phone.service.CallListenerService

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
        Log.e("AutoStartReceiver", "AutoStartReceiver")
        intent!!.setClass(context!!, CallListenerService::class.java)
        context.startService(intent)
    }

}