package com.maiya.call.phone.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.telecom.Call
import android.telecom.TelecomManager
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.ActivityCompat
import com.android.internal.telephony.ITelephony
import com.maiya.call.App
import com.maiya.call.phone.manager.PhoneCallManager
import com.maiya.call.phone.service.NotificationService
import java.io.IOException
import java.lang.reflect.Method


/**
 * 电话操作工具类
 *
 * Author : ymc
 * Date   : 2020/4/29  14:36
 * Class  : PhoneCallManager
 */

object PhoneCallUtil {
    private var audioManager: AudioManager?

    /**
     * 接听电话
     */
    fun answer() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                val telecomManager = App.context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                if (ActivityCompat.checkSelfPermission(App.context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                telecomManager.acceptRingingCall()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                finalAnswer()
            }
            else -> {
                try {
                    val method: Method = Class.forName("android.os.ServiceManager")
                            .getMethod("getService", String::class.java)
                    val binder = method.invoke(null, Context.TELEPHONY_SERVICE) as IBinder
                    val telephony = ITelephony.Stub.asInterface(binder)
                    telephony.answerRingingCall()
                } catch (e: Exception) {
                    finalAnswer()
                }
            }
        }
    }

    private fun finalAnswer() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val mediaSessionManager = App.context.getSystemService("media_session") as MediaSessionManager
                val activeSessions = mediaSessionManager.getActiveSessions(ComponentName(App.context, NotificationService::class.java)) as List<MediaController>
                if (activeSessions.isNotEmpty()) {
                    for (mediaController in activeSessions) {
                        if ("com.android.server.telecom" == mediaController.packageName) {
                            mediaController.dispatchMediaButtonEvent(KeyEvent(0, 79))
                            mediaController.dispatchMediaButtonEvent(KeyEvent(1, 79))
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            answerPhoneAidl()
        }
    }

    private fun answerPhoneAidl() {
        try {
            val keyEvent = KeyEvent(0, 79)
            val keyEvent2 = KeyEvent(1, 79)
            if (Build.VERSION.SDK_INT >= 19) {
                @SuppressLint("WrongConstant") val audioManager = App.context.getSystemService("audio") as AudioManager
                audioManager.dispatchMediaKeyEvent(keyEvent)
                audioManager.dispatchMediaKeyEvent(keyEvent2)
            }
        } catch (ex: java.lang.Exception) {
            val intent = Intent("android.intent.action.MEDIA_BUTTON")
            intent.putExtra("android.intent.extra.KEY_EVENT", KeyEvent(0, 79) as Parcelable)
            App.context.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED")
            val intent2 = Intent("android.intent.action.MEDIA_BUTTON")
            intent2.putExtra("android.intent.extra.KEY_EVENT", KeyEvent(1, 79) as Parcelable)
            App.context.sendOrderedBroadcast(intent2, "android.permission.CALL_PRIVILEGED")
        }
    }

    /**
     * 断开电话，包括来电时的拒接以及接听后的挂断
     */
    fun disconnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            with(PhoneCallManager.instance) {
                if (!hasDefaultCall()) {
                    return@with
                }
                mainCallId?.let {
                    val result = disconnect(it)
                    if (result) {
                        return
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val telecomManager = App.context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (ActivityCompat.checkSelfPermission(App.context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            telecomManager.endCall()
        } else {
            try {
                val method: Method = Class.forName("android.os.ServiceManager")
                        .getMethod("getService", String::class.java)
                val binder = method.invoke(null, Context.TELEPHONY_SERVICE) as IBinder
                val telephony = ITelephony.Stub.asInterface(binder)
                telephony.endCall()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /**
     * 4.1版本以上接听电话
     */
    private fun answerRingingCall_4_1(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //模拟无线耳机的按键来接听电话
// for HTC devices we need to broadcast a connected headset
        val broadcastConnected = "HTC".equals(Build.MANUFACTURER, ignoreCase = true) && !audioManager.isWiredHeadsetOn
        if (broadcastConnected) {
            broadcastHeadsetConnected(context)
        }
        try {
            try {
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK))
            } catch (e: IOException) { // Runtime.exec(String) had an I/O problem, try to fall back
                val enforcedPerm = "android.permission.CALL_PRIVILEGED"
                val btnDown = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_HEADSETHOOK))
                val btnUp = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_HEADSETHOOK))
                context.sendOrderedBroadcast(btnDown, enforcedPerm)
                context.sendOrderedBroadcast(btnUp, enforcedPerm)
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(context)
            }
        }
    }

    /**
     * 对HTC的手机，需要进行一点特殊的处理，也就是通过广播的形式，让手机误以为连上了无线耳机。
     */
    private fun broadcastHeadsetConnected(context: Context) {
        val i = Intent(Intent.ACTION_HEADSET_PLUG)
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)
        i.putExtra("state", 0)
        i.putExtra("name", "mysms")
        try {
            context.sendOrderedBroadcast(i, null)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 打开免提
     */
    fun openSpeaker() {
        audioManager?.mode = AudioManager.MODE_IN_CALL
        audioManager?.isSpeakerphoneOn = true
    }

    /**
     * 销毁资源
     */
    fun destroy() {
        call = null
        audioManager = null
    }

    var call: Call? = null

    init {
        audioManager = App.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }


}
