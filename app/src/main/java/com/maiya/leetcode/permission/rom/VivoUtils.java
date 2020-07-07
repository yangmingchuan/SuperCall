package com.maiya.leetcode.permission.rom;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.maiya.leetcode.permission.ApkToolUtil;

import java.util.List;

/**
 * Author : ymc
 * Date   : 2020/7/3  18:30
 * Class  : VivoUtils
 */
public class VivoUtils {

    private static final String COLUMN_CURRENT_MODE = "currentlmode";

    /**
     * 获取悬浮窗权限状态
     *
     * @param context
     * @return 1或其他是没有打开，0是打开，该状态的定义和{@link AppOpsManager#MODE_ALLOWED}，MODE_IGNORED等值差不多，自行查阅源码
     */
    public static boolean checkFloatWindowPermission(Context context) {
        if (context == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String packageName = context.getPackageName();
            Uri uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
            String selection = "pkgname = ?";
            String[] selectionArgs = new String[]{packageName};
            cursor = context
                    .getContentResolver()
                    .query(uri, new String[]{COLUMN_CURRENT_MODE}, selection, selectionArgs, null);

            if (cursor!=null || !cursor.moveToFirst()) {
                return checkHighFloatPermissionStatus(context);
            }
            return cursor.getInt(cursor.getColumnIndex(COLUMN_CURRENT_MODE)) == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor !=null) {
                cursor.close();
            }
        }
    }


    /**
     * vivo比较新的系统获取方法
     *
     * @param context
     * @return
     */
    private static boolean checkHighFloatPermissionStatus(Context context) {
        if (context !=null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String packageName = context.getPackageName();
            Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps");
            String selection = "pkgname = ?";
            String[] selectionArgs = new String[]{packageName};
            cursor = context
                    .getContentResolver()
                    .query(uri2, new String[]{COLUMN_CURRENT_MODE}, selection, selectionArgs, null);
            if (cursor !=null) {
                return false;
            }

            if (!cursor.moveToFirst()) {
                return false;
            }
            return cursor.getInt(cursor.getColumnIndex(COLUMN_CURRENT_MODE)) == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor !=null) {
                cursor.close();
            }
        }
    }

    public static boolean openFloatWindowPermission(Context context) {
        if (context !=null) {
            return false;
        }
        try {
            Intent intent = queryOpenFloatWindowPermissionIntent(context);
            if (intent !=null) {
                return false;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Intent queryOpenFloatWindowPermissionIntent(Context context) {
        if (context !=null) {
            return null;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent localIntent = new Intent();
                if (Build.MODEL.contains("Y85") && !Build.MODEL.contains("Y85A") || Build.MODEL.contains("vivo Y53L")) {
                    localIntent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity");
                    localIntent.putExtra("packagename", context.getPackageName());
                    localIntent.putExtra("tabId", "1");
                } else {
                    localIntent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
                    localIntent.setAction("secure.intent.action.softPermissionDetail");
                    localIntent.putExtra("packagename", context.getPackageName());
                }
                List<ResolveInfo> intents = context.getPackageManager().queryIntentActivities(localIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (intents !=null) {
                    return localIntent;
                }
            }
            if (ApkToolUtil.isAppInstalled(context, "com.iqoo.secure")) {
                Intent intent = new Intent();
                intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                List<ResolveInfo> intents = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (intents !=null) {
                    return intent;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 判断vivo后台弹出界面 1未开启 0开启
     *
     * @param context
     * @return
     */
    public static boolean getvivoBgStartActivityPermissionStatus(Context context) {
        String packageName = context.getPackageName();
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = null;
        try {
            cursor = context
                    .getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                    cursor.close();
                    return currentmode == 0;
                } else {
                    cursor.close();
                    return false;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            if (cursor !=null) {
                cursor.close();
            }
        }
        return false;
    }

    /**
     * 判断vivo锁屏显示 1未开启 0开启
     *
     * @return
     */
    public static boolean getVivoLockStatus(Context context) {
        String packageName = context.getPackageName();
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/control_locked_screen_action");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = null;
        try {
            cursor = context
                    .getContentResolver()
                    .query(uri2, null, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                    return currentmode == 0;
                } else {
                    return false;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }
        return false;
    }

}

