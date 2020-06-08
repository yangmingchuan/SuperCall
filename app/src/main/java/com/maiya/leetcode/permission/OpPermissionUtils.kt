package com.maiya.leetcode.permission

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.maiya.leetcode.permission.rom.*
import com.maiya.leetcode.permission.rom.RomUtils.checkIs360Rom
import com.maiya.leetcode.permission.rom.RomUtils.checkIsHuaweiRom
import com.maiya.leetcode.permission.rom.RomUtils.checkIsMeizuRom
import com.maiya.leetcode.permission.rom.RomUtils.checkIsMiuiRom
import com.maiya.leetcode.permission.rom.RomUtils.checkIsOppoRom

/**
 * 权限工具类
 */

object OpPermissionUtils {

    private const val TAG = "OpPermissionUtils"
    fun checkPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            if (checkIsMiuiRom()) {
                return miuiPermissionCheck(context)
            } else if (checkIsMeizuRom()) {
                return meizuPermissionCheck(context)
            } else if (checkIsHuaweiRom()) {
                return huaweiPermissionCheck(context)
            } else if (checkIs360Rom()) {
                return qikuPermissionCheck(context)
            } else if (checkIsOppoRom()) {
                return oppoROMPermissionCheck(context)
            }
        }
        return commonROMPermissionCheck(context)
    }

    private fun huaweiPermissionCheck(context: Context): Boolean {
        return HuaweiUtils.checkFloatWindowPermission(context)
    }

    private fun miuiPermissionCheck(context: Context): Boolean {
        return MiuiUtils.checkFloatWindowPermission(context)
    }

    private fun meizuPermissionCheck(context: Context): Boolean {
        return MeizuUtils.checkFloatWindowPermission(context)
    }

    private fun qikuPermissionCheck(context: Context): Boolean {
        return QikuUtils.checkFloatWindowPermission(context)
    }

    private fun oppoROMPermissionCheck(context: Context): Boolean {
        return OppoUtils.checkFloatWindowPermission(context)
    }

    private fun commonROMPermissionCheck(context: Context): Boolean { //最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
        return if (checkIsMeizuRom()) {
            meizuPermissionCheck(context)
        } else {
            var result = true
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    val clazz: Class<*> = Settings::class.java
                    val canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context::class.java)
                    result = canDrawOverlays.invoke(null, context) as Boolean
                } catch (e: Exception) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
            }
            result
        }
    }
}