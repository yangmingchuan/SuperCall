package com.maiya.call.phone.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.maiya.call.App;
import com.maiya.call.R;
import com.maiya.call.permission.Rom;
import com.maiya.call.phone.receiver.NotificationBroadcast;
import com.maiya.call.phone.utils.NotificationUtil;

/**
 * 自定义通知
 */


public class CustomNotifyManager {

    private static final String[] BLOCK_PHONES = new String[]{"vivo Y31A", "vivo Y51", "vivo Y31"
            , "vivo Y51e", "vivo Y51A", "vivo Y51t L", "vivo Y51n"};//部分vivo手机不支持自定义样式

    private static final String NUBIA_Z11 = "NX549J";//单独屏蔽这个手机
    private static final String notifyName = "消息推送";
    private static final String notifyDescription = "通知栏";
    public static final int STEP_COUNT_NOTIFY_ID = 100;

    public static final String ACTION_NOTIFICATION_CLICK = "com.ACTION_NOTIFICATION_CLICK";

    private static final String CHANNEL_ID = "channel_megatron_1";
    private static final String CHANNEL_NAME = "SuperCall";

    private static final String CHANNEL_CONTENT = "SuperCall时刻在你身边！";

    private static NotificationManager manager;
    private static CustomNotifyManager instance;

    private boolean isMIUI;

    private CustomNotifyManager() {
        isMIUI = Rom.isMiui();
    }

    public static synchronized CustomNotifyManager getInstance() {
        if (instance == null) {
            instance = new CustomNotifyManager();
        }
        return instance;
    }

    private static synchronized NotificationManager getNotificationManager(Context context, boolean isDefault) {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                if (!isDefault) {
                    importance = NotificationManager.IMPORTANCE_LOW;
                }
                mChannel = new NotificationChannel(CHANNEL_ID, notifyName, importance);
                mChannel.setDescription(notifyDescription);
                if (isDefault) {
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                } else {
                    mChannel.enableLights(false);
                    mChannel.enableVibration(false);
                    mChannel.setVibrationPattern(new long[]{0});
                    mChannel.setSound(null, null);
                }
                NotificationChannelGroup group = new NotificationChannelGroup("stick", "通知");
                manager.createNotificationChannelGroup(group);
                mChannel.setGroup("stick");
                manager.createNotificationChannel(mChannel);
            }
        }
        return manager;
    }

    public Notification getNotifyNotification(Context context) {
        NotificationCompat.Builder builder = getBuilder(context);
        try {
            getNotificationManager(context, false);
            RemoteViews remoteViews = null;
            if (notShieldPhone()) {
                if (isMIUI) {
                    remoteViews = new RemoteViews(context.getPackageName(),
                            R.layout.layout_item_notification_mi);
                } else {
                    remoteViews = new RemoteViews(context.getPackageName(),
                            R.layout.layout_item_notification);
                }
                builder.setCustomContentView(remoteViews);
            }
            Notification notification = builder
                    .setContentTitle(CHANNEL_NAME)
                    .setContentText(CHANNEL_CONTENT)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(null)
                    .setContentIntent(getPendingIntent())
                    .setAutoCancel(true)
                    .setLights(0, 0, 0)
                    .setVibrate(new long[]{0})
                    .setSound(null)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .build();
            if (remoteViews != null) {
                setNotificationItem(context, notification, remoteViews);
            }
            return notification;
        } catch (Exception e) {
            try {
                return getDefaultNotification(builder);
            } catch (Exception E) {
            }
        }
        return null;
    }

    private void setNotificationItem(Context context, Notification notification
            , RemoteViews remoteViews) {
        if (context == null || remoteViews == null) {
            return;
        }
        String title = "SuperCall";
        String content = "守护你的来电进程...";
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_content, content);
    }

    public Notification getDefaultNotification(NotificationCompat.Builder builder) {
        return builder
                .setContentTitle(CHANNEL_NAME)
                .setContentText(CHANNEL_CONTENT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(getPendingIntent())
                .setAutoCancel(true)
                .setLights(0, 0, 0)
                .setVibrate(new long[]{0})
                .setSound(null)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .build();
    }

    private PendingIntent getPendingIntent() {
        Context context = App.Companion.getContext();
        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.setAction(ACTION_NOTIFICATION_CLICK);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPendingIntent(String action) {
        Context context = App.Companion.getContext();
        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Builder getBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && NotificationUtil.findNotificationBuilder()) {
            return new NotificationCompat.Builder(context, CHANNEL_ID);
        }
        return new NotificationCompat.Builder(context);
    }

    /**
     * 屏蔽某个手机,走最普通推送
     */
    private static boolean notShieldPhone() {
        String device = Build.MODEL;
        if (NUBIA_Z11.contains(device)) {
            return false;
        }
        if ("5.1.1".equals(Build.VERSION.RELEASE)) {//vivo 5.1.1部分手机不支持自定义样式
            for (String blockPhone : BLOCK_PHONES) {
                if (blockPhone.equals(device)) {
                    return false;
                }
            }
        }
        return true;
    }

}
