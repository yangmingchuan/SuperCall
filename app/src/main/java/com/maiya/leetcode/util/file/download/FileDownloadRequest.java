package com.maiya.leetcode.util.file.download;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.File;


public class FileDownloadRequest {

    public static void download(String url, File target, int type, FileDownloadCallback callback) {
        if (!TextUtils.isEmpty(url) && target != null) {
            FileDownloadTask task = new FileDownloadTask(url, target, type, callback);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
