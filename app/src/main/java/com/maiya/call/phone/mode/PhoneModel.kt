package com.maiya.call.phone.mode

import com.maiya.call.phone.bean.PhoneMsg

/**
 * Author : ymc
 * Date   : 2020/5/25  19:08
 * Class  : PhoneModel
 */

object PhoneModel : BaseModel() {

    fun seachMsgByPhoneNumber(content: String, callBack: Callback<PhoneMsg>?) {
        val params = mutableMapOf<String, String>().also {
            it["phone"] = content
        }
        HttpUtils.postWithPhpEncrypt(UrlConstants.PHONE_ATTRIBUTION, params, object : RequestDataCallback<ServerPhoneMsg>() {
            override fun onSuccess(serverData: ServerPhoneMsg?) {
                    safeCallSuccess(callBack,  BeanTransformUtils.toPhoneMsg(serverData))
            }

            override fun onFailure(errorMsg: String?) {
                safeCallFailed(callBack, errorMsg)
            }
        })
    }

}

