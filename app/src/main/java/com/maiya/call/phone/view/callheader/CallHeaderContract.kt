package com.maiya.call.phone.view.callheader

import android.content.Context
import com.maiya.call.base.BasePre
import com.maiya.call.base.BaseView
import com.maiya.call.phone.utils.ContactUtil


/**
 * Created by ymc on 2020/11/19.
 * @Description 电话头部信息契约类
 */

class CallHeaderContract {

    interface View : BaseView {

        fun onQueryLocalContactInfoSuccessful(info: ContactUtil.ContactInfo?)

        fun updateCallingTime(callId: String, time: String)

        fun onQueryPhoneInfoSuccessful(city: String?, type: String?)
    }

    interface Presenter : BasePre<View> {

        fun startTimer(s: String?)

        fun removeTime(callId: String?)

        fun stopTimer(callId: String?)

        fun queryLocalContactInfo(context: Context, phoneNum: String)

        fun queryPhoneInfo(phoneNum: String)

        fun formatPhoneNumber(phoneNum: String?): String?
    }
}
