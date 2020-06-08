package com.maiya.leetcode.permission.rom

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.util.Log

/**
 * 360 rom 工具类
 */

object QikuUtils {
    private const val TAG = "QikuUtils"
    /**
     * 检测 360 悬浮窗权限
     */
    @JvmStatic
    fun checkFloatWindowPermission(context: Context): Boolean {
        val version = Build.VERSION.SDK_INT
        return if (version >= 19) {
            checkOp(context, 24)
        } else true
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun checkOp(context: Context, op: Int): Boolean {
        val version = Build.VERSION.SDK_INT
        if (version >= 19) {
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                val clazz: Class<*> = AppOpsManager::class.java
                val method = clazz.getDeclaredMethod("checkOp",
                        Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
                return AppOpsManager.MODE_ALLOWED == method.invoke(manager, op, Binder.getCallingUid(), context.packageName) as Int
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        } else {
            Log.e("", "Below API 19 cannot invoke!")
        }
        return false
    }

    /**
     * 去360权限申请页面
     */
    fun applyPermission(context: Context) {
        val intent = Intent()
        intent.setClassName("com.android.settings", "com.android.settings.Settings\$OverlaySettingsActivity")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity")
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent)
            } else {
                Log.e(TAG, "can't open permission page with particular name, please use " +
                        "\"adb shell dumpsys activity\" command and tell me the name of the float window permission page")
            }
        }
    }

    private fun isIntentAvailable(intent: Intent?, context: Context): Boolean {
        return if (intent == null) {
            false
        } else context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }
}