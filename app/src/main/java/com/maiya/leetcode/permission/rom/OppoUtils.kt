package com.maiya.leetcode.permission.rom

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.util.Log

/**
 * oppo 工具类
 */

object OppoUtils {
    private const val TAG = "OppoUtils"
    /**
     * 检测 360 悬浮窗权限
     */
    @JvmStatic
    fun checkFloatWindowPermission(context: Context): Boolean {
        val version = Build.VERSION.SDK_INT
        return if (version >= 19) {
            checkOp(context, 24) //OP_SYSTEM_ALERT_WINDOW = 24;
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
            Log.e(TAG, "Below API 19 cannot invoke!")
        }
        return false
    }

    /**
     * oppo ROM 权限申请
     */
    fun applyOppoPermission(context: Context) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //com.coloros.safecenter/.sysfloatwindow.FloatWindowListActivity
            //悬浮窗管理页面
            val comp = ComponentName("com.coloros.safecenter",
                    "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity")
            intent.component = comp
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}