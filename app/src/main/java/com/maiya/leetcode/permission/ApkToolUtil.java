package com.maiya.leetcode.permission;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Author : ymc
 * Date   : 2020/7/7  11:49
 * Class  : ApkToolUtil
 */
public class ApkToolUtil {

    private static final String INSTALL_PACKAGE = "install_package";
    private static final String INSTALL_TIME = "install_time";

    /**
     * apk安装完成后自动唤醒app
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean autoStartApkAfterInstall(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT).send();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 根据包名启动应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean startApk(Context context, String packageName) {
        boolean result = false;
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * @param context
     * @param apkPackageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String apkPackageName) {
        if (TextUtils.isEmpty(apkPackageName)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        boolean result = false;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(apkPackageName, 0);
            if (packageInfo != null) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

}
