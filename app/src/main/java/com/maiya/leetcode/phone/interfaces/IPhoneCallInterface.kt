package com.maiya.leetcode.phone.interfaces

import android.telecom.Call

/**
 * @Author:fkq
 * @Date: 2020-07-06 18:07
 * @Description:
 */
interface IPhoneCallInterface {

    fun onCallStateChanged(call: Call?, state: Int)

}