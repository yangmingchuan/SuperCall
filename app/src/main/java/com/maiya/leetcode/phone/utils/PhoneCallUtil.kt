package com.maiya.leetcode.phone.utils

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.IBinder
import android.telecom.Call
import android.telecom.TelecomManager
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.ActivityCompat
import com.android.internal.telephony.ITelephony
import com.maiya.leetcode.MApplication
import com.maiya.leetcode.phone.service.NotificationService
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
        if (Build.VERSION.SDK_INT >= 28) {
            val telecomManager = MApplication().getInstance().getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (ActivityCompat.checkSelfPermission(MApplication().getInstance(),
                            Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            telecomManager.acceptRingingCall()
        } else {
            try {
                val method: Method = Class.forName("android.os.ServiceManager")
                        .getMethod("getService", String::class.java)
                val binder = method.invoke(null, Context.TELEPHONY_SERVICE) as IBinder
                val telephony = ITelephony.Stub.asInterface(binder)
                telephony.answerRingingCall()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ringTong", "finalAnswer")
                finalAnswer()
            }
        }
    }

    private fun finalAnswer() {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                var mediaSessionManager = MApplication().getInstance().getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
                val activeSessions = mediaSessionManager.getActiveSessions(
                        ComponentName(MApplication().getInstance(), NotificationService::class.java)) as List<MediaController>
                if (activeSessions.isNotEmpty()) {
                    for (mediaController in activeSessions) {
                        if ("com.android.server.telecom" == mediaController.packageName) {
                            mediaController.dispatchMediaButtonEvent(KeyEvent(1, 79))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (th: Throwable) {
        }
    }


    /**r
     * 断开电话，包括来电时的拒接以及接听后的挂断
     */
    fun disconnect() {

        if (Build.VERSION.SDK_INT >= 28) {
            val telecomManager = MApplication().getInstance().getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (ActivityCompat.checkSelfPermission(MApplication().getInstance(), Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            telecomManager?.endCall()
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
     * 打开免提
     */
    fun openSpeaker() {
        if (audioManager != null) {
            audioManager!!.mode = AudioManager.MODE_IN_CALL
            audioManager!!.isSpeakerphoneOn = true
        }
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
        audioManager = MApplication().getInstance().getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }


}
