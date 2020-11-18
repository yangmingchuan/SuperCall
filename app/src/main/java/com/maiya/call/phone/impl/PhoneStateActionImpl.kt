package com.maiya.call.phone.impl

import android.text.TextUtils
import com.maiya.call.phone.interfaces.IPhoneStateAction
import com.maiya.call.phone.manager.FloatingWindowManager

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
