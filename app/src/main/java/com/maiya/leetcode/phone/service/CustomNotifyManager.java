package com.maiya.leetcode.phone.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.maiya.leetcode.MApplication;
import com.maiya.leetcode.R;
import com.maiya.leetcode.phone.receiver.NotificationBroadcast;
import com.maiya.leetcode.phone.utils.NotificationUtil;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

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
    private static final String CHANNEL_NAME = "荔枝铃声";

    private static final String CHANNEL_CONTENT = "荔枝铃声正在派发大量金币！";

    private static NotificationManager manager;
    private static CustomNotifyManager instance;

    private CustomNotifyManager() {
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
            NotificationChannel mChannel = null;
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

    public Notification getNotifyNotification(Context context, String content, String btn) {
        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.setAction(ACTION_NOTIFICATION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = null;
        NotificationCompat.Builder builder = getBuilder(context);
        try {
            getNotificationManager(context, false);
            RemoteViews remoteViews = null;

            if (notShieldPhone()) {
                // 暂时用同样的布局
                if (NotificationUtil.isDarkNotificationTheme(new MApplication().getInstance())) {
                    remoteViews = new RemoteViews(context.getPackageName(),
                            R.layout.layout_item_notification);
                } else {
                    remoteViews = new RemoteViews(context.getPackageName(),
                            R.layout.layout_item_notification);
                }

                if (!TextUtils.isEmpty(content)) {
                    remoteViews.setTextViewText(R.id.tv_content, content);
                }
                if (!TextUtils.isEmpty(btn)) {
                    remoteViews.setTextViewText(R.id.tv_task, btn);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (remoteViews != null) {
                    builder.setCustomContentView(remoteViews);
                }
                notification = builder
                        .setContentTitle(CHANNEL_NAME)
                        .setContentText(CHANNEL_CONTENT)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(null)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setLights(0, 0, 0)
                        .setVibrate(new long[]{0})
                        .setSound(null)
                        .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                        .build();
                return notification;
            }
            if (remoteViews != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setCustomContentView(remoteViews);
            }
            notification = getDefaultNotification(pendingIntent, builder);
            return notification;
        } catch (Exception e) {
            try {
                notification = getDefaultNotification(pendingIntent, builder);
                return notification;
            } catch (Exception E) {
            }
        }
        return null;
    }

    private Notification getDefaultNotification(PendingIntent pendingIntent, NotificationCompat.Builder builder) {
        return builder
                .setContentTitle(CHANNEL_NAME)
                .setContentText(CHANNEL_CONTENT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(0, 0, 0)
                .setVibrate(new long[]{0})
                .setSound(null)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .build();
    }

    private NotificationCompat.Builder getBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
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

    private static boolean hasProperties(String... properties) {
        // android 8.0，读取 /system/uild.prop 会报 permission denied
        if (Build.VERSION.SDK_INT < 25) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File(getRootDirectoryFile(), "build.prop")));
                for (String property : properties) {
                    if (prop.getProperty(property, null) != null) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取外部存储Android的根目录
     */
    public static File getRootDirectoryFile() {
        if (isSDcardExist()) {
            return Environment.getRootDirectory();
        }
        return null;
    }

    /**
     * 判断存储卡是否存在
     */
    public static boolean isSDcardExist() {
        try {
            return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isEmptyVersionName(String versionName) {
        try {
            Class<?>[] clsArray = new Class<?>[]{String.class};
            Object[] objArray = new Object[]{versionName};
            Class<?> SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method get = SystemPropertiesClass.getDeclaredMethod("get", clsArray);
            String version = (String) get.invoke(SystemPropertiesClass, objArray);
            return TextUtils.isEmpty(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
