package com.maiya.leetcode.phone.utils

import android.app.Activity
import java.util.*

/**
 * activity 管理
 * @author ymc
 */
class ActivityStack {
    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    val topActivity: Activity?
        get() = if (activities.isEmpty()) {
            null
        } else activities[activities.size - 1]

    fun finishTopActivity() {
        if (activities.isNotEmpty()) {
            activities.removeAt(activities.size - 1).finish()
        }
    }

    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            activities.remove(activity)
            activity.finish()
        }
    }

    fun finishActivity(activityClass: Class<*>) {
        for (activity in activities) {
            if (activity.javaClass == activityClass) {
                finishActivity(activity)
            }
        }
    }

    fun finishAllActivity() {
        if (activities.isNotEmpty()) {
            for (activity in activities) {
                activity.finish()
                activities.remove(activity)
            }
        }
    }

    companion object {
        val instance = ActivityStack()
        private val activities: MutableList<Activity> = ArrayList()
    }
}