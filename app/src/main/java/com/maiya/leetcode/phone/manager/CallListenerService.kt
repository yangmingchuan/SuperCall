package com.maiya.leetcode.phone.manager

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.maiya.leetcode.R
import com.maiya.leetcode.phone.PhoneCallActivity
import com.maiya.leetcode.phone.impl.PhoneStateActionImpl
import com.maiya.leetcode.phone.impl.PhoneStateListenerImpl
import com.maiya.leetcode.phone.receiver.AutoStartReceiver
import com.maiya.leetcode.phone.service.CustomNotifyManager
import com.maiya.leetcode.phone.service.TaskServiceBinder
import com.maiya.leetcode.phone.utils.ActivityStack
import com.maiya.leetcode.phone.utils.CacheUtils
import com.maiya.leetcode.phone.utils.PhoneCallUtil
import com.maiya.leetcode.phone.utils.PhoneUtil

/**
 * 电话监听
 * Author : ymc
 * Date   : 2020/4/29  13:55
 * Class  : CallListenerService
 */

class CallListenerService : Service() {

    companion object {
        const val KEY_STATE = "key_state"
        const val KEY_PHONE = "key_phone"

        const val PHONE_CALL_ANSWER = "phone_call_answer"
        const val PHONE_CALL_DISCONNECT = "phone_call_disconnect"

        const val ACTION_PHONE_STATE = "action_phone_state"  //电话状态监听处理
        const val ACTION_PHONE_CALL = "action_phone_call"    //电话操作处理（接听、挂断等）

    }


    private lateinit var phoneStateListener: PhoneStateListener
    private lateinit var telephonyManager: TelephonyManager

    private var callState: Int? = -1
    private var phoneNumber: String? = null
    private val callServiceBinder: TaskServiceBinder = TaskServiceBinder()

    override fun onCreate() {
        super.onCreate()
        forceForeground("","")
        initPhoneStateListener()
    }

    fun forceForeground(content: String, btn: String) {
        try {
            val intent = Intent(this, CallListenerService::class.java)
            ContextCompat.startForegroundService(this, intent)
            val notification = CustomNotifyManager.getInstance().getStepNotifyNotification(this, content, btn)
            startForeground(CustomNotifyManager.STEP_COUNT_NOTIFY_ID, notification)
            Log.e("ymc", "CallListenerService   forceForeground ")
        } catch (e: Exception) {
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_STICKY
        }
        val action = intent.action ?: return START_STICKY
        when (action) {
            ACTION_PHONE_STATE -> {
                callState = intent.getIntExtra(KEY_STATE, -1)
                phoneNumber = intent.getStringExtra(KEY_PHONE)
                dealWithCallAction(callState!!, phoneNumber)
            }
            ACTION_PHONE_CALL -> {
                dispatchAction(intent)
            }
        }
        return START_STICKY
    }

    private fun dispatchAction(intent: Intent) {
        if (intent.hasExtra(PHONE_CALL_DISCONNECT)) {
            PhoneCallUtil.disconnect()
            return
        }
        if (intent.hasExtra(PHONE_CALL_ANSWER)) {
            PhoneCallUtil.answer()
        }

    }

    private fun initPhoneStateListener() {
        Log.e("ringTong", "电话监听初始化")
        phoneStateListener = PhoneStateListenerImpl(applicationContext)
        // 设置来电监听器
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }


    override fun onBind(intent: Intent): IBinder? {
        callServiceBinder.onBind(this)
        return callServiceBinder
    }

    override fun onDestroy() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        // 重新拉起
        sendBroadcast(Intent(AutoStartReceiver.AUTO_START_RECEIVER))
        super.onDestroy()
    }

    //来去电的几个状态
    private fun dealWithCallAction(state: Int, phoneNumber: String?) {
        if(CacheUtils.getString(CacheUtils.KEY_SET_RING_TYPE, "").equals(CacheUtils.getString(CacheUtils.TYPE_RING_VIDEO, ""))){
            return
        }
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                PhoneStateActionImpl.instance.onRinging(phoneNumber)
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                PhoneStateActionImpl.instance.onHandUp()
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                PhoneStateActionImpl.instance.onPickUp(phoneNumber)
            }
            1000 -> {   //拨打电话广播状态
                PhoneStateActionImpl.instance.onCallOut(phoneNumber)
            }
        }
    }
}