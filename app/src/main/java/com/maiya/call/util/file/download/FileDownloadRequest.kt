package com.maiya.call.util.file.download

import android.os.AsyncTask
import android.text.TextUtils
import java.io.File

object FileDownloadRequest {
    fun download(url: String?, target: File?, type: Int, callback: FileDownloadCallback?) {
        if (!TextUtils.isEmpty(url) && target != null) {
            val task = FileDownloadTask(url, target, type, callback)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }
}