package com.maiya.call.phone.impl

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

/**
 * 电话状态 impl
 */

class PhoneStateListenerImpl(val context: Context?) : PhoneStateListener() {

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        when (state) {
            TelephonyManager.CALL_STATE_IDLE -> {
                PhoneStateActionImpl.instance.onHandUp()
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                PhoneStateActionImpl.instance.onRinging(phoneNumber)
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                PhoneStateActionImpl.instance.onPickUp(phoneNumber)
            }
            else -> {
            }
        }

    }
}