package com.maiya.leetcode.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.maiya.leetcode.phone.manager.CallListenerService


/**
 *  电话状态监听
 * Author : ymc
 * Date   : 2020/4/29  15:47
 * Class  : PhoneStateReceiver
 */

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("ymc", "ringtong PhoneStateReceiver")

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
                Log.e("ymc", "ringtong 启动service成功")
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            Log.e("ymc", "ringtong 启动service失败")
        }
    }


}