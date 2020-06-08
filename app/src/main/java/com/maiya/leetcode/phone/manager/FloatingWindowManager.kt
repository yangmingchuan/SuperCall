package com.maiya.leetcode.phone.manager

import android.content.Context
import com.maiya.leetcode.phone.impl.IPhoneCallListenerImpl
import com.maiya.leetcode.phone.utils.CacheUtils
import com.maiya.leetcode.phone.view.FloatingWindow

/**
 * 悬浮窗管理类
 *
 * Author : ymc
 * Date   : 2020/5/11  20:19
 */
class FloatingWindowManager private constructor() {
    var videoLink: String? = null
    var context: Context? = null

    companion object {
        val instance: FloatingWindowManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FloatingWindowManager()
        }
    }

    private var fw: FloatingWindow? = null

    fun initManager(context: Context) {
        this.context = context
    }

    fun show(number: String?, isCallIn: Boolean) {
        if(context != null){
            videoLink = CacheUtils.getString(CacheUtils.SP_FILE_KEY, "http://smallmv.eastday.com/mv/20200601171315831243932_1.mp4")
            fw = FloatingWindow(
                    context,
                    videoLink,
                    IPhoneCallListenerImpl()
            )
            fw?.show(number, isCallIn)
        }
    }

    fun dismiss() {
        fw?.dismiss()
    }
}