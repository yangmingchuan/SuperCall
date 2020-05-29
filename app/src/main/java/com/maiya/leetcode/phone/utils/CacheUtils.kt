package com.maiya.leetcode.phone.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Base64
import com.maiya.leetcode.MApplication.Companion.instance
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

object CacheUtils {
    private const val SP_NAME = "pig"
    const val KEY_SET_RING_TYPE = "key_set_ring_type"
    const val TYPE_RING_VIDEO = "video"

    private val sp: SharedPreferences by lazy {
        instance.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    const val SP_FILE_KEY = "sp_video_key"
    //首页记录计时
    const val SP_RING_TIME_KEY = "sp_ring_time_key"

    @JvmStatic
    fun putBooleanAsync(key: String?, value: Boolean?) {
        sp.edit().putBoolean(key, value!!).apply()
    }

    @JvmStatic
    fun putBoolean(key: String?, value: Boolean?) {
        sp.edit().putBoolean(key, value!!).apply()
    }

    @JvmStatic
    fun getBoolean(key: String?, defValue: Boolean?): Boolean {
        return sp.getBoolean(key, defValue!!)
    }

    @JvmStatic
    fun putString(key: String?, value: String?): Boolean {
        return sp.edit().putString(key, value).commit()
    }

    @JvmStatic
    fun putStringAsync(key: String?, value: String?) {
        sp.edit().putString(key, value).apply()
    }

    @JvmStatic
    fun getString(key: String?, defValue: String?): String? {
        return sp.getString(key, defValue)
    }

    @JvmStatic
    fun putInt(key: String?, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    @JvmStatic
    fun putIntAsync(key: String?, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    @JvmStatic
    fun getInt(key: String?, defValue: Int): Int {
        return sp.getInt(key, defValue)
    }

    @JvmStatic
    fun putLong(key: String?, value: Long) {
        sp.edit().putLong(key, value).apply()
    }

    @JvmStatic
    fun putLongAsync(key: String?, value: Long) {
        sp.edit().putLong(key, value).apply()
    }

    @JvmStatic
    fun getLong(key: String?, defValue: Long): Long {
        return sp.getLong(key, defValue)
    }

    @JvmStatic
    fun putFloat(key: String?, value: Float) {
        sp.edit().putFloat(key, value).apply()
    }

    @JvmStatic
    fun getFloat(key: String?, defValue: Float): Float {
        return sp.getFloat(key, defValue)
    }

    fun <T> saveObjectToShare(key: String?, t: T?): Boolean {
        var result = false
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try { // 存储
            val editor = sp.edit()
            if (t == null) {
                editor.putString(key, "")
                editor.commit()
                return true
            }
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(t)
            // 对byte[]进行Base64编码
            val payCityMapBase64 = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
            editor.putString(key, payCityMapBase64)
            editor.commit()
            result = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                oos?.close()
                baos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun <T> getObjectFromShare(key: String?): T? {
        var bais: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            val payCityMapBase64 = sp.getString(key, "")
            if (payCityMapBase64!!.isEmpty()) {
                return null
            }
            val base64Bytes = Base64.decode(payCityMapBase64, Base64.DEFAULT)
            bais = ByteArrayInputStream(base64Bytes)
            ois = ObjectInputStream(bais)
            return ois.readObject() as T
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                ois?.close()
                bais?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun putHashMapData(key: String?, map: Map<String?, String?>): Boolean {
        var result = false
        if (!map.isEmpty()) {
            try {
                val iterator = map.entries.iterator()
                val `object` = JSONObject()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    `object`.put(entry.key, entry.value)
                }
                putString(key, `object`.toString())
                result = true
            } catch (e: Exception) {
                result = false
                e.printStackTrace()
            }
        }
        return result
    }

    fun getHashMapData(key: String?): HashMap<String, String> {
        val serverValue = getString(key, "")
        val map: HashMap<String, String> = LinkedHashMap()
        if (!TextUtils.isEmpty(serverValue)) {
            try {
                val itemObject = JSONObject(serverValue)
                val names = itemObject.names()
                if (names != null) {
                    for (j in 0 until names.length()) {
                        val name = names.getString(j)
                        val value = itemObject.getString(name)
                        map[name] = value
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return map
    }

    fun clearSP() {
        sp.edit().clear().apply()
    }

    fun remove(key: String?) {
        sp.edit().remove(key).apply()
    }

    operator fun contains(key: String?): Boolean {
        return sp.contains(key)
    }

}