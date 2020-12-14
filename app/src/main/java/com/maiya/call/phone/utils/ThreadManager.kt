package com.maiya.call.phone.utils

import java.util.concurrent.Executors

/**
 * Created by ymc on 2020/11/19.
 *
 * @Description
 */
object ThreadManager {
    private val executorService = Executors.newCachedThreadPool { runnable ->
        val result = Thread(runnable, "call_thread")
        result.isDaemon = false
        result
    }

    fun execute(runnable: Runnable?) {
        executorService.execute(runnable)
    }
}