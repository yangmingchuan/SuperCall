package com.maiya.call.phone.view.callheader

import android.content.Context
import android.os.Build
import android.telecom.Call
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.maiya.call.R
import com.maiya.call.phone.interfaces.IPhoneCallInterface
import com.maiya.call.phone.manager.PhoneCallManager
import com.maiya.call.phone.utils.ContactUtil
import com.maiya.call.phone.utils.imageload.GlideImageLoader
import com.maiya.call.util.LogUtils
import kotlinx.android.synthetic.main.view_caller_header.view.*

/**
 * @ClassName: [CallHeaderView]
 * @Description:
 *
 * Created by admin at 2020-07-08
 * @Email xiaosw0802@163.com
 */
@RequiresApi(Build.VERSION_CODES.M)
class CallHeaderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) ,  {

    private val INCOME_TEXT = "来电"

    private val mCallStateListener = object : IPhoneCallInterface {
        override fun onCallStateChanged(call: Call?, state: Int) {
            LogUtils.e("header: call state = $state, callId = $mCallId", "xsw")
            when (state) {
                //正在呼叫(呼出)
                Call.STATE_DIALING -> {
                    tv_sim_card_info.visibility = View.VISIBLE
                    tv_sim_card.text = "正在呼叫"
                }

                Call.STATE_CONNECTING -> {
                    tv_sim_card_info.visibility = View.VISIBLE
                    tv_sim_card.text = "正在连接"
                }

                Call.STATE_RINGING -> {
                    tv_sim_card_info.visibility = View.GONE
                }

                //正在通话
                Call.STATE_ACTIVE -> {
                    mCallId?.let {
                        tv_sim_card_info.visibility = View.VISIBLE
                        presenter?.startTimer(it)
                    }
                }

                //断开连接中
                Call.STATE_DISCONNECTING -> {
//                    tv_sim_card_info.visibility = View.GONE
                }

                //断开连接
                Call.STATE_DISCONNECTED -> {
//                    mCallId?.let {
//                        presenter?.stopTimer(it, "STATE_DISCONNECTED")
//                    }
                    call?.let {
                        presenter?.removeTime(PhoneCallManager.instance.getCallId(it))
                    }
                }
            }
        }
    }

    private var mCallId: String? = null

    override fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_caller_header, this, true)
    }

    override fun initData() {
    }

    override fun setViewListener() {

    }

    fun bindInfo(phoneNumber: String, callId: String, isAddCall: Boolean) {
        LogUtils.e("bind info: from $mCallId to $callId")
        if (mCallId == callId) {
            return
        }
        PhoneCallManager.instance.unregisterCallStateListener(mCallId, mCallStateListener)
        PhoneCallManager.instance.registerCallStateListener(callId, mCallStateListener)

        if (!isAddCall || PhoneCallManager.instance.getCallById(callId)?.state === Call.STATE_ACTIVE) {
            tv_sim_card_info.visibility = View.VISIBLE
            presenter.startTimer(callId)
        }
        mCallId = callId

        tv_call_number.text = presenter.formatPhoneNumber(phoneNumber)
        PhoneCallManager.instance.getSlotIcon(callId)?.also {
            tv_sim_card_icon.setImageDrawable(it)
        }

        presenter.queryLocalContactInfo(context, phoneNumber)
        presenter.queryPhoneInfo(phoneNumber)
    }

    fun unbindInfo() {
        presenter.stopTimer(mCallId)
    }

    override fun onDestroy() {
        unbindInfo()
        super.onDestroy()
    }

    fun onQueryLocalContactInfoSuccessful(info: ContactUtil.ContactInfo?) {
        info?.let {
            if (it.displayName !=null) {
                tv_call_number.text = it.displayName
            }

            if (it.photoUri !=null) {
                GlideImageLoader.displayImage( it.photoUri,iv_account_head)
            }
            return
        }
        iv_account_head.setImageResource(R.drawable.ic_head_default_passenger)
    }

    fun onQueryPhoneInfoSuccessful(city: String?, type: String?) {
        if (city ==null && type == null) {
            tv_call_number_info.visibility = View.GONE
            return
        }
        tv_call_number_info.visibility = View.VISIBLE

        val flag = PhoneCallManager.instance.getCallById(mCallId)?.let {
            if (it.state === Call.STATE_RINGING) {
                return@let INCOME_TEXT
            }
            return@let ""
        }
        tv_call_number_info?.text = "$city $type$flag"
    }

    fun updateCallingTime(callId: String, time: String) {
        LogUtils.e("updateCallingTime: $mCallId, $callId, $time")
        if (mCallId != callId) {
            return
        }
        tv_call_number_info.text = tv_call_number_info.text.toString().replace(INCOME_TEXT, "")
        tv_sim_card_info.visibility = View.VISIBLE
        tv_sim_card.text = time
    }
}