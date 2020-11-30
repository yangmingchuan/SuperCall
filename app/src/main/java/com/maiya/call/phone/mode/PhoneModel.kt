package com.maiya.call.phone.mode

import com.maiya.call.MApplication
import com.maiya.call.phone.bean.PhoneMsg
import com.maiya.call.phone.utils.MobileNumberUtils

/**
 * Author : ymc
 * Date   : 2020/5/25  19:08
 * Class  : PhoneModel
 */

object PhoneModel : BaseModel() {

    fun searchMsgByPhoneNumber(content: String, callBack: Callback<PhoneMsg>?) {
        var pm = PhoneMsg()
        pm.city = MobileNumberUtils.getGeo(content)
        pm.type = MobileNumberUtils.getCarrier(MApplication(), content, 86)
        safeCallSuccess(callBack, pm)

    }

}

