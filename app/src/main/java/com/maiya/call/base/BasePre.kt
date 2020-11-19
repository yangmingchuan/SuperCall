package com.maiya.call.base

/**
 * Created by ymc on 2020/11/19.
 * @Description
 */
interface BasePre<T : BaseView> {

    /**
     * 注入View
     *
     * @param view view
     */
    fun attachView(view: T?)

    /**
     * 回收View
     */
    fun detachView()

}