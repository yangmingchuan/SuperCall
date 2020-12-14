package com.maiya.call.phone.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.maiya.call.phone.PhoneActivity
import com.maiya.call.phone.manager.CustomNotifyManager
import com.maiya.call.phone.utils.GlobalActivityLifecycleMonitor
import java.lang.reflect.Method

/**
 * 通知广播点击
 *
 * Author : ymc
 * Date   : 2020/5/28  20:45
 * Class  : NotificationBroadcast
 */
class NotificationBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 判断app是否在前台
        val action = intent.action
        collapseStatusBar(context)
        if (GlobalActivityLifecycleMonitor.isAppOnForeground) {
            return
        }
        when (action) {
            CustomNotifyManager.ACTION_NOTIFICATION_CLICK -> {
                val i = Intent(context, PhoneActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }
        }
    }

    private fun collapseStatusBar(context: Context) {
        try {
            val statusBarManager = context.getSystemService("statusbar")
            val collapse: Method
            collapse = statusBarManager.javaClass.getMethod("collapsePanels")
            collapse.invoke(statusBarManager)
        } catch (localException: Exception) {
            localException.printStackTrace()
        }
    }
}