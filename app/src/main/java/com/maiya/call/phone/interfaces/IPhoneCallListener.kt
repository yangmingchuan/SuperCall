package com.maiya.call.phone.interfaces

/**
 * 电话操作接口
 * @author ymc
 */

interface IPhoneCallListener {

    /**
     * 用户点击接听按钮
     */
    fun onAnswer()

    /**
     * 用户点击免提按钮
     */
    fun onOpenSpeaker()

    /**
     * 用户点击挂断按钮
     */
    fun onDisconnect()
}