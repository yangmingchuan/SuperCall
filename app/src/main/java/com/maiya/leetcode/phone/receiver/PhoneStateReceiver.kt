package com.maiya.leetcode.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.maiya.leetcode.phone.service.CallListenerService


/**
 *  电话状态监听
 * Author : ymc
 * Date   : 2020/4/29  15:47
 * Class  : PhoneStateReceiver
 */

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        var manager = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var state = manager.callState
        var action = intent!!.action
        var phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action, true)) {
            state = 1000
        }
        intent.setClass(context, CallListenerService::class.java)
        intent.putExtra(CallListenerService.KEY_STATE, state)
        intent.action = CallListenerService.ACTION_PHONE_STATE
        intent.putExtra(CallListenerService.KEY_PHONE, phoneNumber)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
        }
    }


}