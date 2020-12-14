package com.maiya.call.util.file.download

open class FileDownloadCallback {
    open fun onStart() {}
    open fun onProgress(progress: Int, networkSpeed: Long) {}
    open fun onFailure() {}
    open fun onDone() {}
}