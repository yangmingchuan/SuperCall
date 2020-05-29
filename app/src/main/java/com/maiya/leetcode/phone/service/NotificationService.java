package com.maiya.leetcode.phone.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * 通知使用权
 * <p>
 * Author : ymc
 * Date   : 2020/5/25  21:47
 * Class  : NotificationListener
 */

public class NotificationService extends NotificationListenerService {

    public static void a(Context paramContext) {
        if (paramContext != null) {
            ComponentName localComponentName = new ComponentName(paramContext, NotificationService.class);
            Object localObject = (ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE);
            if (localObject != null) {
                localObject = ((ActivityManager) localObject).getRunningServices(Integer.MAX_VALUE);
                if (localObject != null) {
                    localObject = ((List) localObject).iterator();
                    ActivityManager.RunningServiceInfo localRunningServiceInfo;
                    do {
                        if (!((Iterator) localObject).hasNext()) {
                            break;
                        }
                        localRunningServiceInfo = (ActivityManager.RunningServiceInfo) ((Iterator) localObject).next();
                    } while ((!localRunningServiceInfo.service.equals(localComponentName)) || (localRunningServiceInfo.pid != Process.myPid()));
                }
            }
        }
        for (int i = 1; ; i = 0) {
            if (i == 0) {
                b(paramContext);
            }
            return;
        }
    }

    private static void b(Context paramContext) {
        PackageManager localPackageManager = paramContext.getPackageManager();
        localPackageManager.setComponentEnabledSetting(
                new ComponentName(paramContext, NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        localPackageManager.setComponentEnabledSetting(
                new ComponentName(paramContext, NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("ymc","Notification removed");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.e("ymc","Notification posted");
    }

}
