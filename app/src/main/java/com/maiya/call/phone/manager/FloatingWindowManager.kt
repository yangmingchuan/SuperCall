package com.maiya.call.phone.manager

import android.content.Context
import com.maiya.call.phone.impl.IPhoneCallListenerImpl
import com.maiya.call.phone.utils.CacheUtils
import com.maiya.call.phone.view.FloatingWindow

/**
 * 悬浮窗管理类
 *
 * Author : ymc
 * Date   : 2020/5/11  20:19
 */
class FloatingWindowManager private constructor() {
    var videoLink: String? = null
    var context: Context? = null
    var mp4Url = "http://v3-ppx.ixigua.com/d2445d658173ae705ac90c809113a372/601912da/video/m/2208af78a23ef9b4ccf808f3d5ae853e5aa1166af80d000012cd34460394/?a=1319&br=4808&bt=1202&cd=0%7C0%7C1&ch=0&cr=0&cs=0&cv=1&dr=3&ds=3&er&l=2021020215522901012902420905014359&lr=superb&mime_type=video_mp4&pl=0&qs=0&rc=ajVpdWZkdnYzeDMzZmYzM0ApZjczaTtoPDs7N2lpOmQ6N2dzYTRhLmVpNGBfLS0zMTBzc14xNjAvYzUzLy02NjY0XzY6Yw%3D%3D&vl&vr"

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
            videoLink = CacheUtils.getString(CacheUtils.SP_FILE_KEY, mp4Url)
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