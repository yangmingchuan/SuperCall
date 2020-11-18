package com.maiya.call.util.file;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.maiya.call.util.md5.ParseMD5Util;

import java.io.File;

/**
 * app更新下载工具类
 */
public class UpdateDownloadUtils {
    public static String APK_FILE_NAME = "cache";

    /**
     * 修改升级时下载 apk 的路径
     */
    private static String takeApkFile(Context context) {
        String fileName = "";
        File directory = FileUtils.getExternalFilesDirectory(context, APK_FILE_NAME);
        if (directory != null) {
            fileName = directory.getAbsolutePath();
        }
        return fileName;
    }

    /**
     * app更新下载地址
     *
     * @param downloadUrl
     * @return
     */
    public static String getApkUpdateFileName(String downloadUrl,Context context) {
        String apkName = ParseMD5Util.parseStrToMd5L16(downloadUrl) + ".apk";
        String apkPath = takeApkFile(context) + "/" + apkName;
        return apkPath;
    }


    /**
     * 根据Apk的路径获取版本名称
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static String getApkVersionName(Context context, String apkPath) {
        String version = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, 0);
            if (info == null) {
                return null;
            }
            version = info.versionName; // 得到版本信息
        } catch (Exception e) {

        }
        return version;
    }

    public static boolean getUninatllApkInfo(Context context, String filePath) {
        boolean result = false;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
