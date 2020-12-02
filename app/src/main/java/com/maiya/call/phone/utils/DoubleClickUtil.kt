package com.maiya.call.phone.utils

/**
 * 防止连点工具
 */
object DoubleClickUtil {
    private var lastClickTime: Long = 0
    private const val MIN_CLICK_DELAY_TIME = 1000

    //time - lastClickTime < 0 是防止修改系统时间，防止修改的系统时间小于当前保存的时间，造成一直返回false，导致按钮一直无法点击
    //time - lastClickTime > MIN_CLICK_DELAY_TIME  正常情况
    @get:Synchronized
    val isCommonClick: Boolean
        get() {
            val time = System.currentTimeMillis()
            //time - lastClickTime < 0 是防止修改系统时间，防止修改的系统时间小于当前保存的时间，造成一直返回false，导致按钮一直无法点击
            //time - lastClickTime > MIN_CLICK_DELAY_TIME  正常情况
            return if (time - lastClickTime < 0 || time - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = time
                true
            } else {
                false
            }
        }
}