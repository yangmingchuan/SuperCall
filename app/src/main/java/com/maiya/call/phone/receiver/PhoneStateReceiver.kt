package com.maiya.call.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.maiya.call.phone.impl.PhoneStateActionImpl
import com.maiya.call.phone.service.CallListenerService


/**
 *  电话状态监听
 * Author : ymc
 * Date   : 2020/4/29  15:47
 * Class  : PhoneStateReceiver
 */

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val action = intent?.action
            if (Intent.ACTION_NEW_OUTGOING_CALL == action || TelephonyManager.ACTION_PHONE_STATE_CHANGED == action) {
                try {
                    val manager = it.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    var state = manager.callState
                    val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                    if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action, true)) {
                        state = 1000
                    }
                    dealWithCallAction(state, phoneNumber)
                } catch (e: Exception) {
                }
            }
        }
    }

    //来去电的几个状态
    private fun dealWithCallAction(state: Int?, phoneNumber: String?) {
        when (state) {
            // 来电状态
            TelephonyManager.CALL_STATE_RINGING -> {
                PhoneStateActionImpl.instance.onRinging(phoneNumber)
            }
            // 空闲状态(挂断)
            TelephonyManager.CALL_STATE_IDLE -> {
                PhoneStateActionImpl.instance.onHandUp()
            }
            // 摘机状态(接听)
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                PhoneStateActionImpl.instance.onPickUp(phoneNumber)
            }
            1000 -> {   //拨打电话广播状态
                PhoneStateActionImpl.instance.onCallOut(phoneNumber)
            }
        }
    }


}