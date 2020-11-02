package com.maiya.leetcode.util.file;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;

/**
 * des ：
 * created by ：wuchangbin
 * created on：2019/7/1
 */
public class FileUtils {

    private static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message =
                        "File "
                                + directory
                                + " exists and is "
                                + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    String message =
                            "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

    public static boolean mkdirs(File directory) {
        try {
            forceMkdir(directory);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param file
     * @return result[0] hasPermission   result[1] exists
     */
    public static boolean[] exists(File file) {
        boolean result[] = new boolean[2];
        if (file == null) {
            result[0] = true;
            result[1] = false;
        } else {
            try {
                result[1] = file.exists();
                result[0] = true;
            } catch (Exception e) {
                result[0] = false;
                result[1] = false;
            }
        }
        return result;
    }


    /**
     * 获取文件md5值
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (file == null || !file.isFile()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(file);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            inputStream.close();
            return bytesToHexString(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public synchronized static String getDiskCachePath(Context context) {
        return getDiskCachePath(context, null);
    }

    public synchronized static String getDiskCachePath(Context context, String folderName) {
        File cacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            try {
                cacheDir = context.getExternalCacheDir();
            } catch (Exception e) {
            }
            if (cacheDir == null || !cacheDir.exists()) {
                cacheDir = context.getCacheDir();
            }
        } else {
            cacheDir = context.getCacheDir();
        }
        if (cacheDir == null) {
            return "";
        }
        if (!cacheDir.exists()) // if cacheDir is null throws NullPointerException
            cacheDir.mkdirs();
        String cacheDirPath = cacheDir.getPath();
        if (!TextUtils.isEmpty(folderName)) {
            cacheDirPath += (File.separator + folderName);
            File folder = new File(cacheDirPath);
            if (!folder.exists())
                folder.mkdirs();
        }
        return cacheDirPath;
    }

    /**
     * 获取外部存储的根目录
     */
    public static File getExternalStorageDirFile() {
        if (isSDcardExist()) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    /**
     * 检测文件是否可用
     */
    public synchronized static boolean checkFile(File f) {
        if (f != null && f.exists() && f.canRead()
                && (f.isDirectory() || (f.isFile() && f.length() > 0))) {
            return true;
        }
        return false;
    }

    /**
     * 检测文件是否可用
     */
    public synchronized static boolean checkFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists() && f.canRead() && (f.isDirectory() || f.isFile() && f.length() > 0))
                return true;
        }
        return false;
    }

    /**
     * 获取Android/data/包名/files目录下自定义文件夹内的指定文件
     */
    public static File getExternalFilesDirFile(Context context, String dirName, String fileName) {
        File file = getExternalFilesDirectory(context, dirName);
        if (file != null) {
            file = new File(file, fileName);
        }
        return file;
    }

    /**
     * 获取外部存储的Android/data/包名/files目录
     */
    public static File getExternalFilesDirectory(Context context, String type) {
        return context.getExternalFilesDir(type);
    }

    /**
     * 获取外部存储Android的根目录
     */
    public static File getRootDirectoryFile() {
        if (isSDcardExist()) {
            return Environment.getRootDirectory();
        }
        return null;
    }

    /**
     * 判断存储卡是否存在
     */
    public static boolean isSDcardExist() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取文件大小
     *
     * @param file 文件
     * @return
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file == null) {
            return size;
        }
        File flist[] = file.listFiles();
        if (flist != null) {
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        }
        return size;
    }

    /**
     * 转换文件大小单位
     *
     * @param fileS 文件大小
     * @return
     */
    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS == 0) {
            fileSizeString = "0.00B";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 按名称删除webview数据库文件
     */
    public static void deleteWebViewDb(Context context, String dbName) {
        if (!TextUtils.isEmpty(dbName)) {
            try {
                context.deleteDatabase(dbName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file != null && FileUtils.exists(file)[1]) {
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            } else {
                file.delete();
            }
        }
    }

    public static boolean copyFile(File file, File file2) {
        try {
            InputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            OutputStream fileOutputStream = new FileOutputStream(file2);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            byte[] bArr = new byte[5120];
            while (true) {
                int read = bufferedInputStream.read(bArr);
                if (read != -1) {
                    bufferedOutputStream.write(bArr, 0, read);
                } else {
                    bufferedOutputStream.flush();
                    bufferedInputStream.close();
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                    fileInputStream.close();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
