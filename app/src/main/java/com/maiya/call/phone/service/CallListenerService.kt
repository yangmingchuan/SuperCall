package com.maiya.call.phone.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.maiya.call.App
import com.maiya.call.phone.impl.PhoneStateListenerImpl
import com.maiya.call.phone.manager.CustomNotifyManager
import com.maiya.call.phone.receiver.AutoStartReceiver
import com.maiya.call.phone.utils.PhoneCallUtil

/**
 * 电话监听
 * Author : ymc
 * Date   : 2020/4/29  13:55
 * Class  : CallListenerService
 */

class CallListenerService : Service() {

    companion object {
        const val PHONE_CALL_ANSWER = "phone_call_answer"
        const val PHONE_CALL_DISCONNECT = "phone_call_disconnect"

        const val ACTION_PHONE_CALL = "action_phone_call"    //电话操作处理（接听、挂断等）

    }

    private lateinit var phoneStateListener: PhoneStateListener
    private lateinit var telephonyManager: TelephonyManager

    private val callServiceBinder: TaskServiceBinder = TaskServiceBinder()
    private var notification: Notification? = null

    override fun onCreate() {
        super.onCreate()
        initPhoneStateListener()
    }

    fun forceForeground(intent: Intent) {
        try {
            ContextCompat.startForegroundService(App.context, intent)
            notification = CustomNotifyManager.getInstance().getNotifyNotification(App.context)
            if (notification != null) {
                startForeground(CustomNotifyManager.STEP_COUNT_NOTIFY_ID, notification)
            } else {
                startForeground(CustomNotifyManager.STEP_COUNT_NOTIFY_ID,
                        CustomNotifyManager.getInstance().getDefaultNotification(NotificationCompat.Builder(App.context)))
            }
        } catch (e: Exception) {
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_STICKY
        }
        val action = intent.action ?: return START_STICKY
        when (action) {
            ACTION_PHONE_CALL -> {
                dispatchAction(intent)
            }
        }
        return START_STICKY
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
        stopForeground(true)
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
        phoneStateListener = PhoneStateListenerImpl(applicationContext)
        // 设置来电监听器
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }
}