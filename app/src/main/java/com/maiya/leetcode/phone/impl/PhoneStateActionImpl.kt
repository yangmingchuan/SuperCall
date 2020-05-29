package com.maiya.leetcode.phone.impl

import android.text.TextUtils
import android.util.Log
import com.maiya.leetcode.phone.interfaces.IPhoneStateAction
import com.maiya.leetcode.phone.manager.FloatingWindowManager

/**
 * @author ymc
 */
class PhoneStateActionImpl private constructor() : IPhoneStateAction {

    companion object {
        val instance: PhoneStateActionImpl by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PhoneStateActionImpl()
        }
    }

    override
    fun onCallOut(phoneNumber: String?) {
        Log.e("ymc", "PhoneStateActionImpl  ACTION_CALL_OUT $phoneNumber")
        if (!TextUtils.isEmpty(phoneNumber)) {
            FloatingWindowManager.instance.show(phoneNumber, false)
        }
    }

    override fun onRinging(phoneNumber: String?) {
        Log.e("ymc", "PhoneStateActionImpl  ACTION_RINGING $phoneNumber")
        if (!TextUtils.isEmpty(phoneNumber)) {
            FloatingWindowManager.instance.show(phoneNumber, true)
        }
    }

    override fun onPickUp(phoneNumber: String?) {
        Log.e("ringTon", "PhoneStateActionImpl  ACTION_PICK_UP $phoneNumber")
    }

    override fun onHandUp() {
        Log.e("ymc", "PhoneStateActionImpl  ACTION_HAND_UP ")
        FloatingWindowManager.instance.dismiss()
    }

}
