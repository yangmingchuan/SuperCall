package com.maiya.call.phone.manager

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.maiya.call.MApplication
import com.maiya.call.permission.OpPermissionUtils
import com.maiya.call.permission.rom.MiuiUtils
import com.maiya.call.permission.rom.RomUtils
import com.maiya.call.permission.rom.VivoUtils
import com.maiya.call.util.LogUtils
import com.yanzhenjie.permission.AndPermission

/**
 * 电话显示权限工具类
 */

class CallerShowPermissionManager private constructor() {

    companion object {
        val instance: CallerShowPermissionManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CallerShowPermissionManager()
        }

        /**
         * 视频铃声需要的必要权限
         */
        @JvmStatic
        val PERMISSION_VIDEO_RING = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS
        )

        /**
         * 视频铃声需要的必要权限
         */
        @JvmStatic
        val PERMISSION_VIDEO_RING_2 = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS
        )
    }

    fun checkAndRequestPhonePermission(context: Activity, callBack: CallBack?) {
        val permissions = if (Build.VERSION.SDK_INT >= 28) {
            PERMISSION_VIDEO_RING
        } else {
            PERMISSION_VIDEO_RING_2
        }
        val hasPermissions = AndPermission.hasAlwaysDeniedPermission(context, permissions.toString())
        if (hasPermissions) {
            callSuccess(callBack)
            return
        }
        try {
            AndPermission.with(MApplication().getInstance())
                    .permission(permissions)
                    .onGranted {
                        callSuccess(callBack)
                    }.onDenied{
                        callFailed(callBack)
                    }.start()
        } catch (e: Exception) {
            callFailed(callBack)
        }
    }

    var perArray = arrayListOf<Intent>()

    /**
     * 获取需要打开的权限
     */
    fun setRingPermission(context: Context): Boolean {
        perArray.clear()
        if (!OpPermissionUtils.checkPermission(context)) {
            //跳转到悬浮窗设置
            toRequestFloatWindPermission(context)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {
            //准许系统修改
            opWriteSetting(context)
        }
        if (!isAllowed(context)) {
            //后台弹出权限
            openSettings(context)
        }
        if (!notificationListenerEnable(context)) {
            //通知使用权
            gotoNotificationAccessSetting()
        }
        if (perArray.size != 0) {
            context.startActivities(perArray.toTypedArray())
            return false
        } else {
            LogUtils.e("铃声 高级权限全部同意")
            return true
        }
    }

    /**
     * 通过权限判断获取 展示文本
     */
    fun getPerToContent(context: Context): String {
        val sb = StringBuffer()
        var i = 0
        if (!OpPermissionUtils.checkPermission(context)) {
            //跳转到悬浮窗设置
            i++
            sb.append("$i 、设置来电视频悬浮框\n")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {
            //准许系统修改
            i++
            sb.append("$i 、修改铃声设置权限\n")
        }
        if (!isAllowed(context)) {
            //后台弹出权限
            i++
            sb.append("$i 、开启后台弹出页面\n")
        }
        if (!notificationListenerEnable(context)) {
            //通知使用权
            i++
            sb.append("$i 、开启通知使用权\n")
        }
        if (!isLock(context)) {
            //通知使用权
            i++
            sb.append("$i 、开启锁屏显示\n")
        }
        return sb.toString()
    }

    /**
     * 申请悬浮窗权限
     */
    private fun toRequestFloatWindPermission(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val clazz: Class<*> = Settings::class.java
                val field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION")
                val intent = Intent(field[null].toString())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse("package:" + context.packageName)
                perArray.add(intent)
                return
            }
            val intent2 = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            context.startActivity(intent2)
            return
        } catch (e: Exception) {
            if (RomUtils.checkIsMeizuRom()) {
                try {
                    val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
                    intent.putExtra("packageName", context.packageName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: java.lang.Exception) {
                    LogUtils.e("请在权限管理中打开悬浮窗管理权限")
                }
            }
            LogUtils.e("请在权限管理中打开悬浮窗管理权限")
            return
        }
    }

    /**
     * 判断锁屏显示
     */
    private fun isLock(context: Context): Boolean {
        if (RomUtils.checkIsMiuiRom()) {
            return MiuiUtils.canShowLockView(context)
        } else if (RomUtils.checkIsVivoRom()) {
            return VivoUtils.getVivoLockStatus(context)
        }
        return true
    }

    /**
     * 判断锁屏显示
     */
    private fun isAllowed(context: Context): Boolean {
        if (RomUtils.checkIsMiuiRom()) {
            return MiuiUtils.isAllowed(context)
        } else if (RomUtils.checkIsVivoRom()) {
            return VivoUtils.getvivoBgStartActivityPermissionStatus(context)
        }
        return true
    }


    /**
     * 打开设置（后台弹出 锁屏显示）
     */
    private fun openSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("package:${context.packageName}")
                perArray.add(intent)
            } catch (e: java.lang.Exception) {
                LogUtils.e("请在权限管理中打开后台弹出权限")
            }
        } else {
            LogUtils.e("android 6.0以下")
        }
    }

    /**
     * 系统修改
     */
    private fun opWriteSetting(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse("package:${context.packageName}")
                perArray.add(intent)
            }
        }
    }

    /**
     * 读取系统通知
     */
    private fun gotoNotificationAccessSetting() {
        try {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            perArray.add(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                val intent = Intent()
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val cn = ComponentName("com.android.settings", "com.android.settings.Settings\$NotificationAccessSettingsActivity");
                intent.component = cn
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings")
                perArray.add(intent)
            } catch (ex: Exception) {
                LogUtils.e("获取系统通知失败 e : $ex")
            }
        }
    }


    private fun notificationListenerEnable(context: Context): Boolean {
        var enable = false
        val packageName: String = context.packageName
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        if (flat != null) {
            enable = flat.contains(packageName)
        }
        return enable
    }


    private fun callFailed(callBack: CallBack?) {
        callBack?.onFailed()
    }

    private fun callSuccess(callBack: CallBack?) {
        callBack?.onSuccess()
    }


    interface CallBack {
        fun onSuccess()
        fun onFailed()
    }


}
