package com.maiya.leetcode.phone.impl

import android.content.Intent
import android.util.Log
import com.maiya.leetcode.MApplication
import com.maiya.leetcode.phone.interfaces.IPhoneCallListener
import com.maiya.leetcode.phone.manager.CallListenerService
import com.maiya.leetcode.phone.utils.PhoneCallUtil
import com.maiya.leetcode.phone.view.ForegroundActivity
import java.lang.Exception

/**
 * Created by chenxiangxiang on 2020/5/12.
 */
class IPhoneCallListenerImpl : IPhoneCallListener {

    override fun onAnswer() {
        var mContext = MApplication.instance.getContext()
        try {
            var intent = Intent(mContext, ForegroundActivity::class.java)
            intent.action = CallListenerService.ACTION_PHONE_CALL
            intent.putExtra(CallListenerService.PHONE_CALL_ANSWER, "0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext?.startActivity(intent)
        } catch (e: Exception) {
            Log.e("ymc","startForegroundActivity exception>>$e")
            PhoneCallUtil.answer()
        }
    }

    override fun onOpenSpeaker() {
        PhoneCallUtil.openSpeaker()
    }

    override fun onDisconnect() {
        Log.e("ymc"," onDisconnect")
        var mContext = MApplication.instance.getContext()
        try {
            var intent = Intent(mContext, ForegroundActivity::class.java)
            intent.action = CallListenerService.ACTION_PHONE_CALL
            intent.putExtra(CallListenerService.PHONE_CALL_DISCONNECT, "0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext?.startActivity(intent)
        } catch (e: Exception) {
            Log.e("ymc","startForegroundActivity exception>>$e")
            PhoneCallUtil.disconnect()
        }

    }

}