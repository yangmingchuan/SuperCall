package com.maiya.call.phone.mode

/**
 * @ClassName: [BaseModel]
 * @Description:
 *
 * Created by admin at 2020-05-09
 * @Email xiaosw0802@163.com
 */
open class BaseModel {

    companion object {
        /**
         * 未知错误状态码
         */
        const val CODE_UNKNOWN = -1000

        /**
         * 未知错误信息
         */
        const val MESSAGE_UNKNOWN = "unknown"

        @JvmStatic
        fun <R> safeCallSuccess(callback: Callback<R>?, response: R) {
            if (null == callback) {
                return
            }
            callback.onSuccess(response)
        }

        @JvmStatic
        fun safeCallFailed(callback: Callback<*>?, message: String?) {
            safeCallFailed(callback, CODE_UNKNOWN, message)
        }

        @JvmOverloads
        @JvmStatic
        fun safeCallFailed(callback: Callback<*>?
                                     , code: Int = CODE_UNKNOWN
                                     , message: String? = MESSAGE_UNKNOWN
        ) {
            callback?.onFailed(code, message)
        }
    }
}