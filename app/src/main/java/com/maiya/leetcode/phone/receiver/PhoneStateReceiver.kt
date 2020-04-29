package com.maiya.leetcode.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.maiya.leetcode.phone.manager.CallListenerService

/**
 *  电话状态监听
 * Author : ymc
 * Date   : 2020/4/29  15:47
 * Class  : PhoneStateReceiver
 */

class PhoneStateReceiver : BroadcastReceiver() {

    companion object{
        const val PHONE_STATE = "android.intent.action.PHONE_STATE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context!!.startService(Intent(context, CallListenerService::class.java))
    }

}