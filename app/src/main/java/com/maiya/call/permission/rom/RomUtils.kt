package com.maiya.call.permission.rom

import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * rom 工具类
 */
object RomUtils {
    private const val TAG = "RomUtils"
    private const val KEY_VERSION_MIUI = "ro.miui.ui.version.name"
    private const val KEY_VERSION_EMUI = "ro.build.version.emui"
    private const val KEY_VERSION_OPPO = "ro.build.version.opporom"
    private const val KEY_VERSION_SMARTISAN = "ro.smartisan.version"
    private const val KEY_VERSION_VIVO = "ro.vivo.os.version"

    /**
     * 获取 emui 版本号
     * @return
     */
    fun getEmuiVersion(): Double {
        try {
            val emuiVersion = getSystemProperty(KEY_VERSION_EMUI)
            val version = emuiVersion!!.substring(emuiVersion.indexOf("_") + 1)
            return version.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 4.0
    }

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    fun getMiuiVersion(): Int {
        val version = getSystemProperty(KEY_VERSION_MIUI)
        if (version != null) {
            try {
                return version.substring(1).toInt()
            } catch (e: Exception) {
                Log.e(TAG, "get miui version code error, version : $version")
            }
        }
        return -1
    }

    @JvmStatic
    fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            Log.e(TAG, "Unable to read sysprop $propName", ex)
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Exception while closing InputStream", e)
                }
            }
        }
        return line
    }

    fun checkIsHuaweiRom(): Boolean {
        return Build.MANUFACTURER.contains("HUAWEI")
    }

    /**
     * vivo rom
     */
    fun checkIsVivoRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(KEY_VERSION_VIVO))
    }

    /**
     * check if is miui ROM
     */
    fun checkIsMiuiRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(KEY_VERSION_MIUI))
    }

    fun checkIsMeizuRom(): Boolean { //return Build.MANUFACTURER.contains("Meizu");
        val meizuFlymeOSFlag = getSystemProperty("ro.build.display.id")
        return if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            false
        } else if (meizuFlymeOSFlag!!.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
            true
        } else {
            false
        }
    }

    fun checkIs360Rom(): Boolean { //fix issue https://github.com/zhaozepeng/FloatWindowPermission/issues/9
        return (Build.MANUFACTURER.contains("QiKU")
                || Build.MANUFACTURER.contains("360"))
    }

    fun checkIsOppoRom(): Boolean { //https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo")
    }
}