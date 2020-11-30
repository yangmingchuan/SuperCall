package com.maiya.call.phone.presenter

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.maiya.call.base.BasePresenter
import com.maiya.call.phone.bean.PhoneMsg
import com.maiya.call.phone.manager.ContactManager
import com.maiya.call.phone.view.callheader.CallHeaderContract
import com.maiya.call.phone.manager.PhoneNumberManager
import java.util.concurrent.ConcurrentHashMap

/**
 * 电话头部处理类
 */

@TargetApi(Build.VERSION_CODES.M)
class CallHeaderPresenter(view: CallHeaderContract.View) : BasePresenter<CallHeaderContract.View>(), CallHeaderContract.Presenter {

    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val mCalling by lazy {
        ConcurrentHashMap<String, Int>()
    }

    private var mView: CallHeaderContract.View = view

    private var mCallId: String? = null

    override fun queryLocalContactInfo(context: Context, phoneNum: String) {
        ContactManager.getContentCallLog(context, phoneNum) {
            if (mView == null) {
                return@getContentCallLog
            }
            mView.onQueryLocalContactInfoSuccessful(it)
        }
    }

    override fun queryPhoneInfo(phoneNum: String) {
        PhoneNumberManager.getStageTaskList(phoneNum, object : PhoneNumberManager.OnPhoneListener {

            override fun onSuccess(obj: PhoneMsg?) {
                if (mView == null) {
                    return
                }
                mView.onQueryPhoneInfoSuccessful(obj?.city, obj?.type)
            }

            override fun onFailed(code: Int?, errorMsg: String?) {
            }

        })
    }

    override fun formatPhoneNumber(phoneNum: String?): String? {
        return phoneNum?.let {
            if (it.length == 11) {
                it.substring(0, 3) +
                        " - ${it.substring(3, 7)}" +
                        " - ${it.substring(7)}"
            } else it
        } ?: phoneNum
    }


    override fun startTimer(callId: String?) {
        if (callId == null) {
            return
        }
        mCallId = callId
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
                if (mView!=null) {
                    mView.updateCallingTime(callId, formatCallingTime(mCalling[callId] ?: 0))
                    mHandler.postDelayed(this, 1000)
                    return
                }
            }
        })
    }

    override fun removeTime(callId: String?) {
        callId?.let {
            mCalling.remove(it)
        }
    }

    override fun stopTimer(callId: String?) {
        removeTime(callId)
        mHandler.removeCallbacksAndMessages(null)
    }

    private inline fun formatCallingTime(second: Int): String {
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

    override fun attachView(view: CallHeaderContract.View?) {
        super.attachView(view)
    }

    override fun detachView() {
        super.detachView()
        if(mCallId != null){
            stopTimer(mCallId)
        }
    }

}