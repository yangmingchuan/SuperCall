package com.maiya.call.util

import android.util.Log
import com.maiya.call.util.log.GsonWrapper

/**
 * @ClassName [LogUtils]
 * @Description
 *
 */
object LogUtils {

    /**
     * log single maximum output length.
     */
    private const val MAX_MESSAGE_LEN = 3000

    /** 日志 tag 格式 */
    const val TAG_FORMAT = "%s.%s:L%d"
    val LOG_UTILS_CLASS: String by lazy {
        LogUtils::class.java.name
    }

    private val sMessage by lazy {
        mutableListOf<String>()
    }

    var mPreTag = ""
        private set
    private var mLogLevel = LogLevel.DEBUG

    @JvmStatic
    @JvmOverloads
    fun init(logLevel: LogLevel, preTag: String = "xiaosw-") {
        mLogLevel = logLevel
        mPreTag = preTag
    }

    @JvmStatic
    fun findTag(): String {
        with(Thread.currentThread().stackTrace) {
            var traceOffset = -1
            var lastClassIsLogUtils = false
            for (position in 0 until size) {
                val e: StackTraceElement = get(position)
                val isLogUtils = e.className == LOG_UTILS_CLASS;
                if (!isLogUtils && lastClassIsLogUtils) { // Target Class
                    traceOffset = position
                    break
                }
                lastClassIsLogUtils = isLogUtils
            }
            if (traceOffset === -1) {
                return mPreTag
            }
            with(get(traceOffset)) {
                var stackTrace: String = toString()
                stackTrace = stackTrace.substring(stackTrace.lastIndexOf(40.toChar()), stackTrace.length)
                var tag = "%s.%s%s"
                var callerClazzName: String = className
                callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
                return String.format(tag, callerClazzName, methodName, stackTrace)
            }
        }
        return mPreTag
    }


    private inline fun splitMessageIfNeeded(message: String?): MutableList<String> {
        sMessage.clear()
        message?.let {
            var msg = it
            while (msg.length > MAX_MESSAGE_LEN) {
                sMessage.add(msg.substring(0, MAX_MESSAGE_LEN))
                msg = msg.substring(MAX_MESSAGE_LEN)
            }
            if (msg.isNotEmpty()) {
                sMessage.add(msg)
            }
        }
        return sMessage
    }

    @JvmStatic
    fun isEnable() = mLogLevel != LogLevel.NONE

    @JvmStatic
    @JvmOverloads
    fun println(message: String?, isError: Boolean = false) {
        if (mLogLevel.value <= Log.VERBOSE) {
            splitMessageIfNeeded(message).forEach {
                if (isError) {
                    System.err.println(GsonWrapper.formatJsonToLog(it))
                } else {
                    println(GsonWrapper.formatJsonToLog(it))
                }
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun v(message: String?, tag: String = findTag(), throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.VERBOSE) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.v(tag, GsonWrapper.formatJsonToLog(it), this)
                } ?: Log.v(tag, GsonWrapper.formatJsonToLog(it))
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun d(message: String?, tag: String = findTag(), throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.DEBUG) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.d(tag, GsonWrapper.formatJsonToLog(it), this)
                } ?: Log.d(tag, GsonWrapper.formatJsonToLog(it))
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun i(message: String?, tag: String = findTag(), throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.INFO) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.i(tag, GsonWrapper.formatJsonToLog(it), this)
                } ?: Log.i(tag, GsonWrapper.formatJsonToLog(it))
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun w(message: String?, tag: String = findTag(), throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.WARN) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.w(tag, GsonWrapper.formatJsonToLog(it), this)
                } ?: Log.w(tag, GsonWrapper.formatJsonToLog(it))
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun e(message: String? = "", tag: String = findTag(), throwable: Throwable? = null) {
        message?.let {
            if (mLogLevel.value <= Log.ERROR) {
                splitMessageIfNeeded(message).forEach {
                    throwable?.run {
                        Log.e(tag, GsonWrapper.formatJsonToLog(it), this)
                    } ?: Log.e(tag, GsonWrapper.formatJsonToLog(it))
                }
            }
        }
    }

    @JvmStatic
    fun e(throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.ERROR) {
            throwable?.run {
                Log.e(findTag(), "", this)
            }
        }
    }

    enum class LogLevel(val value: Int) {
        NONE(Int.MAX_VALUE),
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR)
    }

}