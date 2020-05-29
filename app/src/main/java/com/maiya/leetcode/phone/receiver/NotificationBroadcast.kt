package com.maiya.leetcode.phone.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.maiya.leetcode.phone.PhoneActivity
import com.maiya.leetcode.phone.service.CustomNotifyManager
import com.maiya.leetcode.phone.utils.GlobalActivityLifecycleMonitor
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
        if (GlobalActivityLifecycleMonitor.isAppOnForeground()) {
            return
        }
        val action = intent.action
        if (action == CustomNotifyManager.ACTION_NOTIFICATION_CLICK) {
            collapseStatusBar(context)
            val i = Intent(context, PhoneActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("flag", "task")
            context.startActivity(i)
        }
    }

    @SuppressLint("WrongConstant")
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