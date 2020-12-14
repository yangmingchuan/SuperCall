package com.maiya.call.phone.manager

import android.content.Context
import android.os.Looper
import com.maiya.call.phone.utils.ContactUtil
import com.maiya.call.phone.utils.ContactUtil.getContentCallLog
import com.maiya.call.phone.utils.ThreadManager.execute
import com.ymc.ijkplay.utils.AppHandlerUtil

object ContactManager {
    fun getContentCallLog(mContext: Context?, number: String?, callBack: Callback?) {
        execute(Runnable {
            val contentCallLog = getContentCallLog(mContext, number)
            callFinish(contentCallLog, callBack)
        })
    }

    private fun callFinish(log: ContactUtil.ContactInfo?, callBack: Callback?) {
        if (callBack == null) {
            return
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            callBack.onFinish(log)
            return
        }
        AppHandlerUtil.getMainHandler().post(Runnable { callBack.onFinish(log) })
    }

    interface Callback {
        fun onFinish(contentCallLog: ContactUtil.ContactInfo?)
    }
}