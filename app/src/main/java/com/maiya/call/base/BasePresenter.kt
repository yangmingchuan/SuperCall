package com.maiya.call.base


/**
 * Created by ymc on 2020/11/19.
 * @Description
 */

class BasePresenter<T : BaseView> : BasePre<T> {
    private  var mView: T? = null

    override fun detachView() {
        mView = null
    }

    override fun attachView(view: T?) {
        mView = view
    }
}
