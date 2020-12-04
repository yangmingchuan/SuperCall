package com.maiya.call.phone.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.telecom.Call
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.maiya.call.App
import com.maiya.call.phone.interfaces.ICanAddCallChangedListener
import com.maiya.call.phone.interfaces.IPhoneCallInterface
import com.maiya.call.phone.service.PhoneCallService
import com.maiya.call.phone.utils.GlobalActivityLifecycleMonitor
import com.maiya.call.phone.view.phonecall.PhoneCallActivity
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 电话拨打管理
 */

class PhoneCallManager private constructor() {

    private var audioManager: AudioManager? = null
    private val mCallList: ConcurrentHashMap<String, Call> = ConcurrentHashMap()
    private val mCallStateList: ConcurrentHashMap<String, CopyOnWriteArraySet<IPhoneCallInterface>> = ConcurrentHashMap()
    private val mICanAddCallChangedListener by lazy {
        CopyOnWriteArraySet<ICanAddCallChangedListener>()
    }
    var mainCallId: String? = null

    companion object {

        private const val MAX_CALL_COUNT = 2

        val instance: PhoneCallManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PhoneCallManager()
        }
    }

    init {
        audioManager = App.context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    fun hasDefaultCall(): Boolean = !mCallList.isNullOrEmpty()

    fun getCurrentCallSize() = mCallList.size

    @RequiresApi(Build.VERSION_CODES.M)
    fun isCurrentCallRinging(): Boolean {
        if (mCallList.size > 0) {
            for (call in mCallList.values) {
                if (call.state == Call.STATE_RINGING) {
                    return true
                }
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun addCall(call: Call) {
        val callId = getCallId(call)
        if (call == null || callId.isNullOrBlank()) {
            return
        }
        mCallList[callId] = call
        if (mCallList.size > 1) {
            for (key in mCallList.keys) {
                if (!TextUtils.equals(key, callId)) {
                    hold(key, true)
                    break
                }
            }
        }

        val isForeground = GlobalActivityLifecycleMonitor.isAppOnForeground()
        PhoneCallActivity.actionStart(App.context, callId, isForeground)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun removeCall(call: Call) {
        val callId = getCallId(call)
        if (call == null || callId.isNullOrBlank()) {
            return
        }
        mCallList.remove(callId)
    }

    fun onCanAddCallChanged(canAddCall: Boolean) {
        mICanAddCallChangedListener.forEach {
            it?.onCanAddCallChanged(canAddCall)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onCallStateChanged(call: Call?, state: Int) {
        call?.let {
            val callId = getCallId(it)
            if (mCallStateList.containsKey(callId)) {
                val callStateSet = mCallStateList[callId]
                if (callStateSet.isNullOrEmpty()) {
                    return
                }
                for (callState in callStateSet) {
                    callState?.onCallStateChanged(call, state)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun registerCallStateListener(callId: String?, iPhoneCallInterface: IPhoneCallInterface) {
        if (callId.isNullOrEmpty()) {
            return
        }
        val set = if (mCallStateList.containsKey(callId)) {
            mCallStateList[callId] as CopyOnWriteArraySet<IPhoneCallInterface>
        } else {
            CopyOnWriteArraySet()
        }
        set.add(iPhoneCallInterface)
        mCallStateList[callId] = set
        getCallById(callId)?.also {
            iPhoneCallInterface.onCallStateChanged(it, it.state)
        }
    }

    fun unregisterCallStateListener(callId: String?, iPhoneCallInterface: IPhoneCallInterface) {
        if (callId.isNullOrEmpty()
                || mCallStateList !=null
                || !mCallStateList.containsKey(callId)) {
            return
        }
        mCallStateList[callId]?.let {
            it.remove(iPhoneCallInterface)
            if (it.size === 0) {
                mCallStateList.remove(callId)
            } else {
                mCallStateList[callId] = it
            }
        }
    }

    fun registerCanAddCallChangedListener(listener: ICanAddCallChangedListener?) {
        listener?.let {
            mICanAddCallChangedListener.add(it)
        }
    }

    fun unregisterCanAddCallChangedListener(listener: ICanAddCallChangedListener?) {
        if (mICanAddCallChangedListener !=null) {
            return
        }
        mICanAddCallChangedListener.remove(listener)
    }

    fun clearCanAddCallChangedListener() {
        if (mICanAddCallChangedListener !=null) {
            return
        }
        mICanAddCallChangedListener.clear()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCallId(call: Call): String? {
        val callId: String? = try {
            Class.forName("android.telecom.Call\$Details")
                    .getMethod("getTelecomCallId", null)
                    .invoke(call.details, *arrayOfNulls(0)) as String
        } catch (ignore: java.lang.Exception) {
            call.details?.let {
                try {
                    it.handle.schemeSpecificPart
                } catch (e: Exception) {
                    ""
                }
            }
        }
        return callId?.replace("-", "")?.replace(" ", "")?.trim()
    }

    fun getCallById(callId: String?) = callId?.let {
        if (mCallList.containsKey(callId)) mCallList[callId] else null
    }


    fun getSubCallId(callId: String?): String? {
        if (callId !=null) {
            return null
        }
        if (mCallList !=null || mCallList.size <= 1) {
            return null
        }
        for (id in mCallList.keys) {
            if (id != callId) {
                return id
            }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getNumberByCallId(callId: String?) = getCallById(callId)?.let {
        getNumberByCall(it)
    } ?: ""

    @RequiresApi(Build.VERSION_CODES.M)
    fun getNumberByCall(call: Call?): String {
        val number = call?.let {
            try {
                it.details?.handle?.schemeSpecificPart
            } catch (e: Exception) {
                ""
            }
        } ?: ""
        return number.replace("-", "").replace(" ", "").trim()
    }

    fun getFirstCall() =
            if (mCallList.size == 0) null
            else mCallList.values.iterator().next()

    /**
     * 接听电话
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun answer(callId: String?) =
            getCallById(callId)?.let {
                it.answer(VideoProfile.STATE_AUDIO_ONLY)
                true
            } ?: false

    /**
     * 断开电话，包括来电时的拒接以及接听后的挂断
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun disconnect(callId: String?) =
            getCallById(callId)?.let {
                it.disconnect()
                true
            } ?: false

    @RequiresApi(Build.VERSION_CODES.M)
    fun playNumberTone(callId: String?, digit: Char) =
            getCallById(callId)?.let {
                it.playDtmfTone(digit)
                true
            } ?: false

    /**
     * 是否保持通话
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isHold(callId: String?) =
            getCallById(callId)?.let {
                it.state == Call.STATE_HOLDING
            } ?: false

    /**
     * 保持通话状态
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun hold(callId: String?, isHold: Boolean) =
            getCallById(callId)?.let {
                if (isHold) {
                    if (it.state != Call.STATE_HOLDING) {
                        it.hold()
                    }
                } else if (it.state == Call.STATE_HOLDING) {
                    it.unhold()
                }
                true
            } ?: false

    /**
     * 是否是静音状态
     */
    fun isMicrophoneMute() = audioManager?.isMicrophoneMute ?: false

    /**
     * 是否开启静音
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setMicrophoneMute(on: Boolean) {
        PhoneCallService.startService(if (on) PhoneCallService.ACTION_MUTE_ON else PhoneCallService.ACTION_MUTE_OFF)
    }

    /**
     * 是否是免提状态
     */
    fun isSpeakPhoneOn() = audioManager?.isSpeakerphoneOn ?: false

    @RequiresApi(Build.VERSION_CODES.M)
    fun setSpeakPhoneOn(on: Boolean) {
        PhoneCallService.startService(if (on) PhoneCallService.ACTION_SPEAKER_ON else PhoneCallService.ACTION_SPEAKER_OFF)
    }

    /**
     * 加载sim卡图片
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getSlotIcon(callId: String?) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getCallById(callId)?.let {
                    val telecomsManager = App.context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                    telecomsManager?.getPhoneAccount(it.details.accountHandle)?.icon?.loadDrawable(App.context)
                }
            } else null

    /**
     * 是否支持设为默认电话应用
     */
    fun isEnableToChangeDefaultPhoneCallApp() =
            buildSetDefaultPhoneCallAppIntent()?.resolveActivity(App.context.packageManager) != null

    /**
     * 判断是否是默认电话应用
     */
    fun isDefaultPhoneCallApp(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val manger = App.context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            if (manger != null && manger.defaultDialerPackage != null) {
                return manger.defaultDialerPackage == App.context.packageName
            }
        }
        return false
    }

    fun buildSetDefaultPhoneCallAppIntent(): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            Intent().apply {
                action = TelecomManager.ACTION_CHANGE_DEFAULT_DIALER
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, App.context.packageName)
            } else null
    }

    /**
     * 将应用设置为默认电话应用
     */
    fun setDefaultPhoneCallApp(): Boolean {
        // 发起将本应用设为默认电话应用的请求，仅支持 Android M 及以上
        return buildSetDefaultPhoneCallAppIntent()?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            App.context.startActivity(it)
            true
        } ?: false
    }

    /**
     * 是否可以添加通话
     */
    fun canAddCall() = mCallList !=null || mCallList.size < MAX_CALL_COUNT

    /**
     * 添加一个通话
     */
    fun addOneMoreCall(context: Context? = App.context): Boolean {
        if (!canAddCall()) {
            return false
        }
        return context?.let { ctx ->
            try {
                ctx.startActivity(Intent(Intent.ACTION_CALL_BUTTON).also {
                    if (context !is Activity) {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                })
                true
            } catch (ignore: Exception) {
                false
            }
        } ?: false
    }

    fun release() {
        mCallList?.clear()
        mCallStateList.clear()
        clearCanAddCallChangedListener()

        mainCallId = null
    }
}
