package com.maiya.call.phone.impl

import android.content.Intent
import android.util.Log
import com.maiya.call.App
import com.maiya.call.phone.interfaces.IPhoneCallListener
import com.maiya.call.phone.service.CallListenerService
import com.maiya.call.phone.utils.PhoneCallUtil
import com.maiya.call.phone.view.ForegroundActivity
import java.lang.Exception


class IPhoneCallListenerImpl : IPhoneCallListener {

    override fun onAnswer() {
        val mContext = App.context
        try {
            val intent = Intent(mContext, ForegroundActivity::class.java)
            intent.action = CallListenerService.ACTION_PHONE_CALL
            intent.putExtra(CallListenerService.PHONE_CALL_ANSWER, "0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
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
        val mContext = App.context
        try {
            val intent = Intent(mContext, ForegroundActivity::class.java)
            intent.action = CallListenerService.ACTION_PHONE_CALL
            intent.putExtra(CallListenerService.PHONE_CALL_DISCONNECT, "0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
        } catch (e: Exception) {
            Log.e("ymc","startForegroundActivity exception>>$e")
            PhoneCallUtil.disconnect()
        }

    }

}