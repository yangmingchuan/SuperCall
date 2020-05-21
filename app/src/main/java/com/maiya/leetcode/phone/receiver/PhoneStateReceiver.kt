package com.maiya.leetcode.phone.receiver

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import android.view.KeyEvent
import com.android.internal.telephony.ITelephony
import java.io.IOException
import java.lang.reflect.Method


//import com.maiya.leetcode.phone.utils.Phone2Utils

/**
 *  电话状态监听
 * Author : ymc
 * Date   : 2020/4/29  15:47
 * Class  : PhoneStateReceiver
 */

class PhoneStateReceiver : BroadcastReceiver() {

    companion object {
        const val PHONE_STATE = "android.intent.action.PHONE_STATE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        //context!!.startService(Intent(context, PhoneCallService::class.java))
        //context!!.startService(Intent(context, CallListenerService::class.java))

        var tm = context!!.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager

        when (tm.callState) {
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.i("onCallStateChanged", "CALL_STATE_OFFHOOK");
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                Log.i("onCallStateChanged", "CALL_STATE_RINGING");
                //Phone2Utils.autoAnswerPhone(context, tm);
                answerRingingCall_4_1(context)
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.i("onCallStateChanged", "CALL_STATE_IDLE");
            }
        }
    }

    fun endCall(){
        try {
            val method: Method = Class.forName("android.os.ServiceManager").getMethod("getService", String::class.java)
            val binder = method.invoke(null, arrayOf<Any>(Context.TELEPHONY_SERVICE)) as IBinder
            val telephony = ITelephony.Stub.asInterface(binder)
            telephony.endCall()
        }catch (e :Exception ){
            e.printStackTrace()
        }

    }

    /**
     * 接听电话
     */
    fun answerRingingCall() {
        try {
            val method = Class.forName("android.os.ServiceManager").getMethod("getService", String::class.java)
            val binder = method.invoke(null, *arrayOf<Any>(Context.TELEPHONY_SERVICE)) as IBinder
            val telephony = ITelephony.Stub.asInterface(binder)
            telephony.answerRingingCall()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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
                val btnDown: Intent = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_HEADSETHOOK))
                val btnUp: Intent = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
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


}