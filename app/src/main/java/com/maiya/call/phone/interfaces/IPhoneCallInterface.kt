package com.maiya.call.phone.interfaces

import android.telecom.Call

interface IPhoneCallInterface {

    fun onCallStateChanged(call: Call?, state: Int)

}