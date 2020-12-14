package com.maiya.call.util.context

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import com.maiya.call.util.LogUtils.i

/**
 * Author : ymc
 * Date   : 2020/7/7  14:04
 * Class  : ContextUtils
 */
object ContextUtils {
    fun isDestroyed(context: Context?): Boolean {
        val activity = findActivity(context)
        return if (activity == null) {
            true
        } else {
            var isDestroyed = activity.isDestroyed
            if (activity is FragmentActivity) {
                val supportFragmentManager = activity.supportFragmentManager
                isDestroyed = supportFragmentManager.isDestroyed
            }
            i("activity.getClass().getName()>>" + activity.javaClass.name)
            isDestroyed
        }
    }

    fun findActivity(context: Context?): Activity? {
        return if (context == null) {
            null
        } else {
            if (context is Activity) {
                context
            } else if (context is ContextWrapper) {
                findActivity(context.baseContext)
            } else {
                null
            }
        }
    }
}