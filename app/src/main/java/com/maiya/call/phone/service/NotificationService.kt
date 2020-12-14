package com.maiya.call.phone.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * 通知使用权
 *
 *
 * Author : ymc
 * Date   : 2020/5/25  21:47
 * Class  : NotificationListener
 */
class NotificationService : NotificationListenerService() {
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.e("ymc", "Notification removed")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.e("ymc", "Notification posted")
    }
}