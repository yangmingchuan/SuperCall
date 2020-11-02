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
        if (!TextUtils.isEmpty(phoneNumber)) {
            FloatingWindowManager.instance.show(phoneNumber, false)
        }
    }

    override fun onRinging(phoneNumber: String?) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            FloatingWindowManager.instance.show(phoneNumber, true)
        }
    }

    override fun onPickUp(phoneNumber: String?) {
    }

    override fun onHandUp() {
        FloatingWindowManager.instance.dismiss()
    }

}
