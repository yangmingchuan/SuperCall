package com.maiya.call.phone.service;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * 通知使用权
 * <p>
 * Author : ymc
 * Date   : 2020/5/25  21:47
 * Class  : NotificationListener
 */

public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("ymc", "Notification removed");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.e("ymc", "Notification posted");
    }

}
