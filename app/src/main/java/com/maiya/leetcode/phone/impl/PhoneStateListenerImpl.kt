package com.maiya.leetcode.phone.impl

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

/**
 * 电话状态 impl
 */

class PhoneStateListenerImpl(val context: Context?) : PhoneStateListener() {

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        when (state) {
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.e("ringTon", "通话空闲")
                PhoneStateActionImpl.instance.onHandUp()
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                Log.e("ringTon", "摘机状态，接听或者拨打$phoneNumber")
                PhoneStateActionImpl.instance.onRinging(phoneNumber)
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.e("ringTon", "响铃:来电号码$phoneNumber")
                PhoneStateActionImpl.instance.onPickUp(phoneNumber)
            }
            else -> {
            }
        }

    }
}