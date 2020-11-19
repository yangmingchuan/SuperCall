package com.maiya.call.phone.view.callheader

import android.content.Context
import com.maiya.call.base.BasePre

import com.maiya.call.base.BaseView


/**
 * Created by ymc on 2020/11/19.
 * @Description 电话头部信息契约类
 */

class CallHeaderContract {

    interface View : BaseView {
        fun getHotListErr(err: String?)
    }

    interface Presenter : BasePre<View> {
        val hotListResult: Unit

        fun saveHistory(context: Context?, historyList: List<String?>?)
        fun getHistoryList(context: Context?, historyList: List<String?>?)
    }
}
