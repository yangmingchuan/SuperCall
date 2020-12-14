package com.maiya.call.util.file

import android.content.Context
import android.content.pm.PackageManager
import com.maiya.call.util.md5.ParseMD5Util

/**
 * app更新下载工具类
 */
object UpdateDownloadUtils {
    var APK_FILE_NAME = "cache"

    /**
     * 修改升级时下载 apk 的路径
     */
    private fun takeApkFile(context: Context): String {
        var fileName = ""
        val directory = FileUtils.getExternalFilesDirectory(context, APK_FILE_NAME)
        if (directory != null) {
            fileName = directory.absolutePath
        }
        return fileName
    }

    /**
     * app更新下载地址
     *
     * @param downloadUrl
     * @return
     */
    fun getApkUpdateFileName(downloadUrl: String?, context: Context): String {
        val apkName = ParseMD5Util.parseStrToMd5L16(downloadUrl) + ".apk"
        return takeApkFile(context) + "/" + apkName
    }

    /**
     * 根据Apk的路径获取版本名称
     *
     * @param context
     * @param apkPath
     * @return
     */
    fun getApkVersionName(context: Context, apkPath: String?): String? {
        var version: String? = null
        try {
            val pm = context.packageManager
            val info = pm.getPackageArchiveInfo(apkPath, 0) ?: return null
            version = info.versionName // 得到版本信息
        } catch (e: Exception) {
        }
        return version
    }

    fun getUninatllApkInfo(context: Context, filePath: String?): Boolean {
        var result = false
        try {
            val pm = context.packageManager
            val info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES)
            if (info != null) {
                result = true
            }
        } catch (e: Exception) {
            result = false
        }
        return result
    }
}