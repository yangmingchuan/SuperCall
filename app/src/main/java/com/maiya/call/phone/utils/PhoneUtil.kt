package com.maiya.call.phone.utils

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils
import com.maiya.call.phone.service.CallListenerService

/**
 * Author : ymc
 * Date   : 2020/4/29  15:09
 * Class  : PhoneUtil
 */

object PhoneUtil {

    /**
     * 格式化电话号码
     */
    fun formatPhoneNumber(phoneNum: String): String? {
        return if (!TextUtils.isEmpty(phoneNum) && phoneNum.length == 11) {
            (phoneNum.substring(0, 3) + "-"
                    + phoneNum.substring(3, 7) + "-"
                    + phoneNum.substring(7))
        } else phoneNum
    }

    fun getCallingTime(callingTime: Int = 0): String? {
        val minute: Int = callingTime / 60
        val second: Int = callingTime % 60
        return (if (minute < 10) "0$minute" else minute).toString() +
                ":" +
                if (second < 10) "0$second" else second
    }

    /**
     * 判断service 是否启动
     */
    fun isServiceRunning(serviceClass: Class<CallListenerService>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager ?: return false
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}