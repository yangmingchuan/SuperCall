package com.maiya.leetcode.permission

import android.content.Context
import android.os.Build
import android.provider.Settings
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

    fun checkPermission(context: Context): Boolean { //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
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
            } else if (RomUtils.checkIsVivoRom()) {
                return vivoROMPermissionCheck(context)
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

    private fun vivoROMPermissionCheck(context: Context): Boolean {
        return VivoUtils.checkFloatWindowPermission(context)
    }

    private fun commonROMPermissionCheck(context: Context): Boolean { //最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
        return if (checkIsMeizuRom()) {
            meizuPermissionCheck(context)
        } else if (RomUtils.checkIsVivoRom()) {
            VivoUtils.checkFloatWindowPermission(context)
        } else {
            var result = true
            if (Build.VERSION.SDK_INT >= 23) {
                result = Settings.canDrawOverlays(context)
            }
            result
        }
    }
}