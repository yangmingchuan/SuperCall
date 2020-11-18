package com.maiya.call.phone.interfaces

/**
 * 电话状态接口
 */

interface IPhoneStateAction {

    /**
     * 拨打电话
     */
    fun onCallOut(phoneNumber: String?)

    /**
     * 响铃
     */
    fun onRinging(phoneNumber: String?)

    /**
     * 接听电话
     */
    fun onPickUp(phoneNumber: String?)

    /**
     * 挂断电话
     */
    fun onHandUp()

}