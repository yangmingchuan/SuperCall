package com.maiya.leetcode.util.log

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.maiya.leetcode.util.LogUtils
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.SoftReference
import java.lang.reflect.Type

/**
 * @ClassName [GsonWrapper]
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object GsonWrapper {

    private const val LINE_BORDER = "═════════════════════════" +
            "══════════════════════════════════════════"


    private var sGsonRef: SoftReference<Gson> = SoftReference(buildGson())

    private inline fun buildGson() = GsonBuilder().disableHtmlEscaping().create()

    @JvmStatic
    fun gson() : Gson {
        if (null == sGsonRef || sGsonRef.get() == null) {
            synchronized(this)     {
                if (null == sGsonRef || sGsonRef.get() == null) {
                    sGsonRef = SoftReference(buildGson())
                }
            }
        }
        return sGsonRef.get()!!
    }

    @JvmStatic
    fun <T> fromJson(json: String, clazz: Class<T>) : T = gson().fromJson(json, clazz)

    @JvmStatic
    fun <T> fromJson(json: String?, typeOfT: Type) : T = gson().fromJson(json, typeOfT)

    @JvmStatic
    fun toJson(obj: Any) = gson().toJson(obj)

    /**
     * 格式化 log
     */
    fun formatJsonToLog(log: String) = if (!LogUtils.isEnable()|| !isJson(log)) {
        StringBuilder("\t\n╔").append(LINE_BORDER)
                .append("\n║").append(log)
                .append("\n╚").append(LINE_BORDER).append("\n ").toString()
    } else StringBuilder("\n╔").append(LINE_BORDER)
            .append("\n║").append(log)
            .append("\n║").append(LINE_BORDER)
            .append("\n║").append(formatJsonStr(log))
            .append("\n╚").append(LINE_BORDER).append("\n ").toString()

    private fun getLevelStr(level: Int): String {
        val levelStr = StringBuffer()
        for (i in 0 until level) {
            levelStr.append("    ")
        }
        return levelStr.toString()
    }

    /**
     * 格式化 json
     */
    @JvmStatic
    fun formatJsonStr(jsonData: String): String {
        var jsonStr = jsonData
        if (!isJson(jsonStr)) {
            return jsonStr
        }
        jsonStr = jsonStr.trim { it <= ' ' }
        var level = 0
        var lastChar = ' '
        val jsonFormatStr = StringBuffer()
        val len = jsonStr.length
        for (i in 0 until len) {
            val c = jsonStr[i]
            if (level > 0 && '\n' == jsonFormatStr[jsonFormatStr.length - 1]) {
                jsonFormatStr.append(getLevelStr(level))
            }
            when (c) {
                '{', '[' -> {
                    if (lastChar != ',') {
                        jsonFormatStr.append(getLevelStr(level))
                    }
                    jsonFormatStr.append(c).append("\n║")
                    level++
                }

                ',' -> jsonFormatStr.append(c)
                        .append("\n║")
                        .append(getLevelStr(level))

                '}', ']' -> {
                    level--
                    jsonFormatStr.append("\n║")
                            .append(getLevelStr(level))
                            .append(c)
                }

                else -> {
                    if (lastChar == '[' || lastChar == '{') {
                        jsonFormatStr.append(getLevelStr(level))
                    }
                    jsonFormatStr.append(c)
                }
            }
            lastChar = c
        }
        return jsonFormatStr.toString()
    }

    /**
     * 判断字符串是否为 json 格式
     */
    @JvmStatic
    inline fun isJson(json: String): Boolean {
        var json = json
        if (TextUtils.isEmpty(json)) {
            return false
        }
        json = json.trim { it <= ' ' }
        try {
            if (json.startsWith("[")) {
                JSONArray(json)
                return true
            } else if (json.startsWith("{")) {
                JSONObject(json)
                return true
            }
        } catch (e: Exception) {
            // not need to do anything.
        }

        return false
    }

}