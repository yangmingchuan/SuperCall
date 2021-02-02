package com.maiya.call.phone.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper


/**
 * Created by ymc on 2/2/21.
 * @Description
 */

object AppHandlerUtil {
    private val BACKGROUND_HANDLER_THREAD = HandlerThread("app_background_thread")
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private var backgroundHandler: Handler? = null
    fun getMainHandler(): Handler {
        return mainHandler
    }

    fun getBackgroundHandler(): Handler? {
        if (backgroundHandler == null) {
            synchronized(AppHandlerUtil::class.java) {
                if (backgroundHandler == null) {
                    BACKGROUND_HANDLER_THREAD.start()
                    backgroundHandler = Handler(BACKGROUND_HANDLER_THREAD.looper)
                }
            }
        }
        return backgroundHandler
    }

    fun runInUiThread(runnable: Runnable?) {
        getMainHandler().post(runnable)
    }

    fun runInBackgroundThread(runnable: Runnable?) {
        getBackgroundHandler()?.post(runnable)
    }
}