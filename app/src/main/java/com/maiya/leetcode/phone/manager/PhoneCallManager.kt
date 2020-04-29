package com.maiya.leetcode.phone.manager

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.telecom.Call
import android.telecom.VideoProfile
import androidx.annotation.RequiresApi

/**
 * Author : ymc
 * Date   : 2020/4/29  14:36
 * Class  : PhoneCallManager
 */
@RequiresApi(Build.VERSION_CODES.M)
class PhoneCallManager(context: Context) {
    private var context: Context?
    private var audioManager: AudioManager?
    /**
     * 接听电话
     */

    fun answer() {
        if (call != null) {
            call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
            openSpeaker()
        }
    }

    /**
     * 断开电话，包括来电时的拒接以及接听后的挂断
     */
    fun disconnect() {
        if (call != null) {
            call!!.disconnect()
        }
    }

    /**
     * 打开免提
     */
    fun openSpeaker() {
        if (audioManager != null) {
            audioManager!!.mode = AudioManager.MODE_IN_CALL
            audioManager!!.isSpeakerphoneOn = true
        }
    }

    /**
     * 销毁资源
     */
    fun destroy() {
        call = null
        context = null
        audioManager = null
    }

    companion object {
        var call: Call? = null
    }

    init {
        this.context = context
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
}
