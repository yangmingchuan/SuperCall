package com.maiya.leetcode.phone.manager

import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi
import com.maiya.leetcode.phone.PhoneCallActivity
import com.maiya.leetcode.phone.utils.ActivityStack
import com.maiya.leetcode.phone.utils.CallType

/**
 * 监听电话通信状态的服务
 * TODO 华为手机 启动不起来 InCallService
 * Author : ymc
 * Date   : 2020/4/29  14:38
 */

@RequiresApi(api = Build.VERSION_CODES.M)
class PhoneCallService : InCallService() {

    private val callback: Call.Callback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            when (state) {
                Call.STATE_ACTIVE -> {

                }
                Call.STATE_DISCONNECTED -> {
                    ActivityStack().finishActivity(PhoneCallActivity::class.java)
                }
            }
        }
    }

    /**
     * call加入 （呼入 /呼出）
     */
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Log.e("ymc","InCallService监听服务--call 呼入/呼出")
        call.registerCallback(callback)
        PhoneCallManager.call = call
        var callType: CallType? = null
        if (call.state == Call.STATE_RINGING) {
            callType = CallType.CALL_IN
        } else if (call.state == Call.STATE_CONNECTING) {
            callType = CallType.CALL_OUT
        }
        if (callType != null) {
            val details = call.details
            val phoneNumber = details.handle.schemeSpecificPart
            val intent = Intent(applicationContext, PhoneCallActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Intent.EXTRA_MIME_TYPES, callType)
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber)
            startActivity(intent)
        }
    }

    /**
     * 呼入/呼出取消
     */
    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.e("ymc","InCallService监听服务 -- 呼入/呼出取消")
        call.unregisterCallback(callback)
        PhoneCallManager.call = null
        var activityStack = ActivityStack.instance
        //呼入取消 移除界面
        if(activityStack.topActivity!!.packageName!!.contentEquals("PhoneCallActivity")){
            activityStack.finishTopActivity()
        }
    }

}