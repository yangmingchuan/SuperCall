package com.maiya.leetcode.permission.rom

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
    /**
     * 获取 emui 版本号
     * @return
     */
    val emuiVersion: Double
        get() {
            try {
                val emuiVersion = getSystemProperty("ro.build.version.emui")
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
    val miuiVersion: Int
        get() {
            val version = getSystemProperty("ro.miui.ui.version.name")
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

    @JvmStatic
    fun checkIsHuaweiRom(): Boolean {
        return Build.MANUFACTURER.contains("HUAWEI")
    }

    /**
     * check if is miui ROM
     */
    @JvmStatic
    fun checkIsMiuiRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))
    }

    @JvmStatic
    fun checkIsMeizuRom(): Boolean {
        val meizuFlymeOSFlag = getSystemProperty("ro.build.display.id")
        return if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            false
        } else meizuFlymeOSFlag!!.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")
    }

    @JvmStatic
    fun checkIs360Rom(): Boolean {
        return (Build.MANUFACTURER.contains("QiKU")
                || Build.MANUFACTURER.contains("360"))
    }

    @JvmStatic
    fun checkIsOppoRom(): Boolean {
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo")
    }
}