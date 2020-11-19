package com.maiya.call.phone.presenter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.maiya.call.phone.bean.PhoneMsg
import com.maiya.call.phone.utils.ContactUtils
import com.preface.megatron.tel.manager.PhoneNumberManager
import java.util.concurrent.ConcurrentHashMap

/**
 * @ClassName: [CallHeaderPresenter]
 * @Description:
 *
 * Created by admin at 2020-07-08
 * @Email xiaosw0802@163.com
 */
@TargetApi(Build.VERSION_CODES.M)
class CallHeaderPresenter {

    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val mCalling by lazy {
        ConcurrentHashMap<String, Int>()
    }

    inline fun queryLocalContactInfo(context: Context, phoneNum: String) {
        ContactUtils.getContentCallLog(context, phoneNum) {
            if (_isDestroyed()) {
                return@getContentCallLog
            }
            getView()?.onQueryLocalContactInfoSuccessful(it)
        }
    }

    inline fun queryPhoneInfo(phoneNum: String) {
        PhoneNumberManager.getStageTaskList(phoneNum, object : PhoneNumberManager.OnPhoneListener {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(phoneMsg: PhoneMsg?) {
                if (_isDestroyed()) {
                    return
                }
                getView()?.onQueryPhoneInfoSuccessful(phoneMsg?.city, phoneMsg?.type)
            }

            override fun onFailed(code: Int?, errorMsg: String?) {
            }

        })
    }

    fun formatPhoneNumber(phoneNum: String?): String? {
        return phoneNum?.let {
            if (it.length == 11) {
                it.substring(0, 3) +
                        " - ${it.substring(3, 7)}" +
                        " - ${it.substring(7)}"
            } else it
        } ?: phoneNum
    }

    fun startTimer(callId: String) {
        if (!mCalling.containsKey(callId)) {
            mCalling[callId] = 0
        }
        stopTimer(null)
        mHandler.post(object : Runnable {
            override fun run() {
                mHandler.removeCallbacks(this)
                for (entry in mCalling) {
                    entry?.let {
                        it.setValue(it.value + 1)
                    }
                }
                if (!_isDestroyed()) {
                    getView()?.updateCallingTime(callId, formatCallingTime(mCalling[callId] ?: 0))
                    mHandler.postDelayed(this, 1000)
                    return
                }
            }
        })
    }

    fun removeTime(callId: String?) {
        callId?.let {
            mCalling.remove(it)
        }
    }

    fun stopTimer(callId: String?) {
        removeTime(callId)
        mHandler.removeCallbacksAndMessages(null)
    }

    private inline fun formatCallingTime(second: Int) : String {
        val hour = second / 3600
        val minute = second % 3600 / 60
        val second = second % 60
        val sb = StringBuffer()
        if (hour > 0) {
            sb.append(String.format("%02d:", hour))
        }
        return sb.append(String.format("%02d:", minute))
                .append(String.format("%02d", second))
                .toString()
    }

}