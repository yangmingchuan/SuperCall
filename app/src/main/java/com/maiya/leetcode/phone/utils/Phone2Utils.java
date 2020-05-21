package com.maiya.leetcode.phone.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Author : ymc
 * Date   : 2020/5/21  20:19
 * Class  : Phone2Utils
 */
public class Phone2Utils {

    static String TAG = "PhoneUtils";
    /**
     * 从TelephonyManager中实例化ITelephony,并返回
     */
    static public ITelephony getITelephony(TelephonyManager telMgr)
            throws Exception {
        @SuppressLint("SoonBlockedPrivateApi")
        Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod(
                "getITelephony");
        getITelephonyMethod.setAccessible(true);// 私有化函数也能使用
        return (ITelephony) getITelephonyMethod.invoke(telMgr);
    }

    //自动接听
    public static void autoAnswerPhone(Context c, TelephonyManager tm) {
        try {
            Log.i(TAG, "autoAnswerPhone");
            ITelephony itelephony = getITelephony(tm);
            // itelephony.silenceRinger();
            itelephony.answerRingingCall();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Log.e(TAG, "用于Android2.3及2.3以上的版本上");
                Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
                c.sendOrderedBroadcast(intent,
                        "android.permission.CALL_PRIVILEGED");
                intent = new Intent("android.intent.action.MEDIA_BUTTON");
                keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
                c.sendOrderedBroadcast(intent,
                        "android.permission.CALL_PRIVILEGED");
                Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
                localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                localIntent1.putExtra("state", 1);
                localIntent1.putExtra("microphone", 1);
                localIntent1.putExtra("name", "Headset");
                c.sendOrderedBroadcast(localIntent1,
                        "android.permission.CALL_PRIVILEGED");
                Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
                KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                localIntent2.putExtra("android.intent.extra.KEY_EVENT",
                        localKeyEvent1);
                c.sendOrderedBroadcast(localIntent2,
                        "android.permission.CALL_PRIVILEGED");
                Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
                KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                localIntent3.putExtra("android.intent.extra.KEY_EVENT",
                        localKeyEvent2);
                c.sendOrderedBroadcast(localIntent3,
                        "android.permission.CALL_PRIVILEGED");
                Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
                localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                localIntent4.putExtra("state", 0);
                localIntent4.putExtra("microphone", 1);
                localIntent4.putExtra("name", "Headset");
                c.sendOrderedBroadcast(localIntent4,
                        "android.permission.CALL_PRIVILEGED");
            } catch (Exception e2) {
                e2.printStackTrace();
                Intent meidaButtonIntent = new Intent(
                        Intent.ACTION_MEDIA_BUTTON);
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                c.sendOrderedBroadcast(meidaButtonIntent, null);
            }
        }
    }

    //自动挂断
    public static void endPhone(Context c,TelephonyManager tm) {
        try {
            Log.i(TAG, "endPhone");
            ITelephony iTelephony;
            Method getITelephonyMethod = TelephonyManager.class
                    .getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            iTelephony = (ITelephony) getITelephonyMethod.invoke(tm,
                    (Object[]) null);
            // 挂断电话
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
