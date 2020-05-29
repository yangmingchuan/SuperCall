package com.maiya.leetcode.phone.manager

import android.content.Context
import com.maiya.leetcode.phone.impl.IPhoneCallListenerImpl
import com.maiya.leetcode.phone.utils.CacheUtils
import com.preface.megatron.tel.impl.IPhoneCallListenerImpl
import com.preface.megatron.tel.view.FloatingWindow
import com.qsmy.business.common.storage.sp.CacheUtils

/**
 * 悬浮窗管理类
 *
 * Author : ymc
 * Date   : 2020/5/11  20:19
 * Class  : FloatingWindowImpl
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
            videoLink = CacheUtils.getString(CacheUtils.SP_FILE_KEY, "http://smallmv.eastday.com/mv/20200416174500687851552_1.mp4")
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