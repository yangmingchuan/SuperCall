package com.maiya.leetcode.phone.service;

import android.app.Notification;
import android.app.NotificationChannel;
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
import com.maiya.leetcode.phone.utils.CacheUtils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 自定义通知
 */


public class CustomNotifyManager {
    // 小米系统相关参数，用于判断是否是小米系统
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    private static final String[] BLOCK_PHONES = new String[]{"vivo Y31A", "vivo Y51", "vivo Y31"
            , "vivo Y51e", "vivo Y51A", "vivo Y51t L", "vivo Y51n"};//部分vivo手机不支持自定义样式

    private static final String NUBIA_Z11 = "NX549J";//单独屏蔽这个手机
    private static final String notifyName = "消息推送";
    private static final String notifyDescription = "通知栏";
    public static final int STEP_COUNT_NOTIFY_ID = 100;

    public static final String ACTION_NOTIFICATION_CLICK = "com.ACTION_NOTIFICATION_CLICK";
    private static final String CHANNEL_ID = "channel_megatron_1";
    private static final String CHANNEL_NAME = "荔枝铃声";


    private static NotificationManager manager;
    private static CustomNotifyManager instance;

    private boolean isMIUI;

    private CustomNotifyManager() {
        isMIUI = isMIUI();
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
                manager.createNotificationChannel(mChannel);
            }
        }
        return manager;
    }

    /**
     * 本地步数推送
     */
    public Notification getStepNotifyNotification(Context context, String content, String btn) {
        getNotificationManager(context, false);
        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.setAction(ACTION_NOTIFICATION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        RemoteViews remoteViews = null;
        if (notShieldPhone()) {
            // 自定义布局
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.layout_item_notification);
            PendingIntent homeIntent = PendingIntent.getBroadcast(MApplication.Companion.getInstance().getContext(), 1,
                    new Intent(ACTION_NOTIFICATION_CLICK), PendingIntent.FLAG_CANCEL_CURRENT);
            if (!TextUtils.isEmpty(content)) {
                remoteViews.setTextViewText(R.id.tv_content, content);
            }
            if (!TextUtils.isEmpty(btn)) {
                remoteViews.setTextViewText(R.id.tv_task, btn);
            }
            remoteViews.setOnClickPendingIntent(R.id.tv_task, homeIntent);
            builder.setCustomContentView(remoteViews);
        }
        Notification notification = builder
                .setContentTitle(CHANNEL_NAME)
                .setContentText("ymc 常驻Notification")
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

    /**
     * 判断是否是小米系统
     */
    private static boolean isMIUI() {
        // 获取缓存状态
        String isMIUIStr = CacheUtils.getString("isMIUISystem", "");
        if (!TextUtils.isEmpty(isMIUIStr)) {
            return isMIUIStr.equals("true");
        }
        boolean isMIUISystem = isMIUISystem();
        CacheUtils.putString("isMIUISystem", isMIUISystem + "");
        return isMIUISystem;
    }

    /**
     * 判断是否是小米系统
     */
    private static boolean isMIUISystem() {
        return hasProperties(KEY_MIUI_VERSION_CODE, KEY_MIUI_VERSION_NAME, KEY_MIUI_INTERNAL_STORAGE)
                || !isEmptyVersionName(KEY_MIUI_VERSION_NAME);
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
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
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
