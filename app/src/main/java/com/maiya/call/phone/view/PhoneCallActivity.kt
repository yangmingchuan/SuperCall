package com.maiya.call.phone.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.telecom.Call
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.maiya.call.MApplication
import com.maiya.call.R
import com.maiya.call.phone.interfaces.ICanAddCallChangedListener
import com.maiya.call.phone.interfaces.IPhoneCallInterface
import com.maiya.call.phone.manager.PhoneCallManager
import com.maiya.call.phone.manager.PhoneRecordManager
import com.maiya.call.util.LogUtils
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_phone_call.*

/**
 *
 */

@RequiresApi(api = Build.VERSION_CODES.M)
class PhoneCallActivity : AppCompatActivity(), View.OnClickListener
        , SensorEventListener, ICanAddCallChangedListener {

    companion object {
        private const val EXTRA_MAIN_CALL_ID = "extra_main_call_id"
        private const val EXTRA_SUB_CALL_ID = "extra_sub_call_id"
        private const val EXTRA_IS_FOREGROUND = "extra_is_foreground"

        fun actionStart(context: Context, mainCallId: String?, isForeground: Boolean?) {
            val intent = Intent(context, PhoneCallActivity::class.java)
            if (context !is Activity) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent.putExtra(EXTRA_MAIN_CALL_ID, mainCallId)
            intent.putExtra(EXTRA_SUB_CALL_ID, PhoneCallManager.instance.getSubCallId(mainCallId))
            intent.putExtra(EXTRA_IS_FOREGROUND, isForeground)
            context.startActivity(intent)
        }
    }

    private var mNearFace = false
    private var mSensor: Sensor? = null
    private var mSensorManager: SensorManager? = null
    private var mWakeLock: PowerManager.WakeLock? = null

    private var mCallerKeyboardAdapter: CallerKeyboardAdapter? = null

    private val innerAnimationSet by lazy { AnimationSet(true) }
    private val outerAnimationSet by lazy { AnimationSet(true) }

    private var mMainCallId = ""
        set(value) {
            field = value
            PhoneCallManager.instance.mainCallId = field
        }
    private var mSubCallId: String? = null

    private var isForeground = false
    private var recordManager: PhoneRecordManager? = null
    private var mCallStateListener = object : IPhoneCallInterface {
        override fun onCallStateChanged(call: Call?, state: Int) {
            LogUtils.e("onCallStateChaned: $mMainCallId, $mSubCallId state = $state")
            updateActionState()
            when (state) {
                Call.STATE_RINGING -> toggleCallInView(true)
                Call.STATE_ACTIVE -> {
                    toggleCallInView(false)
                    call_hold_container.handleHold(mSubCallId)
                }

                Call.STATE_HOLDING -> {
                }

                //断开连接
                Call.STATE_DISCONNECTED -> {
                    if (recordManager.isRecording == true) {
                        recordManager.stopRecord()
                    }
                    if (PhoneCallManager.instance.getCurrentCallSize() <= 1) {
                        call_hold_container.hide()
                    }
                    toggleRecordStatus(false)
                    checkFinish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_call)
        mSensorManager = MApplication().getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        if (mSensorManager != null) {
            mSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        }
        val powerManager = MApplication().getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager != null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "cs:phoneCallActivityTag")
        }
        if (mSensor != null) {
            mSensorManager?.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //hide navigationBar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.decorView.systemUiVisibility = uiOptions

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        // turn on and unlock screen
        window?.let {
            presenter?.showOnLockScreen(it)
        }
        call_hold_container.callSwitchListener = object : CallHoldView.CallSwitchListener {
            override fun onCallSwitch(callId: String) {
                mSubCallId = mMainCallId
                mMainCallId = callId
                call_hold_container.handleHold(mSubCallId)
                initCall(false)
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        PhoneCallManager.instance.registerCanAddCallChangedListener(this)
        parseIntent(intent)
        initCall(true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseIntent(intent)
        initCall(true)
    }

    private inline fun parseIntent(intent: Intent?) {
        intent?.let {
            if (Utils.isEmpty(mMainCallId)) {
                isForeground = it.getBooleanExtra(EXTRA_IS_FOREGROUND, false)
            }
            mMainCallId = it.getStringExtra(EXTRA_MAIN_CALL_ID) ?: ""
            mSubCallId = it.getStringExtra(EXTRA_SUB_CALL_ID)
        }
    }

    override fun onCanAddCallChanged(canAddCall: Boolean) {
        LogUtils.e("canAddCall: $canAddCall")
        updateActionState(canAddCall)
    }

    override fun setViewListener() {
        ll_call_in?.findViewById<View>(R.id.tv_phone_hang_up)?.setOnClickListener(this)
        ll_call_in?.findViewById<View>(R.id.tv_phone_pick_up)?.setOnClickListener(this)
        tv_add_call.setOnClickListener(this)
        cb_hold.setOnClickListener(this)
        cb_recording.setOnClickListener(this)
        cb_speaker.setOnClickListener(this)
        iv_hand_up.setOnClickListener(this)
        cb_mute.setOnClickListener(this)
        tv_show_caller_keyboard.setOnClickListener(this)
        tv_hide_keyboard.setOnClickListener(this)
    }

    private fun initCall(isAddCall: Boolean) {
        if (PhoneCallManager.instance.getCallById(mMainCallId) == null) {
            checkFinish()
            return
        }
        if (mMainCallId == mSubCallId) {
            return
        }
        with(PhoneCallManager.instance) {
            hold(mMainCallId, false)
            unregisterCallStateListener(mSubCallId, mCallStateListener)
            registerCallStateListener(mMainCallId, mCallStateListener)
            val phoneNum = getNumberByCallId(mMainCallId)
            updateActionState()
            caller_header_container.bindInfo(phoneNum, mMainCallId, isAddCall)
            if (recordManager?.isRecording == true) {
                recordManager?.stopRecord()
            }
            toggleRecordStatus(false)
            recordManager = PhoneRecordManager(phoneNum)
            LogUtils.e("initCall: mMainCallId = $mMainCallId, mSubCallId = $mSubCallId, size = ${getCurrentCallSize()}")
        }

    }

    override fun isFitsSystemWindows(): Boolean {
        return false
    }

    override fun onDestroy() {
        caller_header_container.unbindInfo()
        PhoneCallManager.instance.unregisterCallStateListener(mMainCallId, mCallStateListener)
        PhoneCallManager.instance.unregisterCanAddCallChangedListener(this)
        PhoneCallManager.instance.mainCallId = null
        PhoneCallManager.instance.release()
        if (mSensorManager != null) {
            mSensorManager?.unregisterListener(this)
        }
        if (mWakeLock?.isHeld == true) {
            mWakeLock?.release()
        }
        mWakeLock = null
        super.onDestroy()
        LogUtils.e("onDestroy:", "xsw")
    }

    override fun finishAndRemoveTask() {
        super.finishAndRemoveTask()
        if (!isForeground) {
            ExitAppHelper.moveTaskToBack(this)
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            if (v.id === R.id.cb_hold) {
                (it as? CheckBox)?.let { checkBox ->
                    checkBox.isChecked = !checkBox.isChecked
                }
            }
            if (!DoubleClickUtil.isCommonClick()) {
                updateActionState()
                return
            }
            when (it.id) {
                R.id.tv_phone_hang_up -> PhoneCallManager.instance.disconnect(mMainCallId)

                R.id.tv_phone_pick_up -> PhoneCallManager.instance.answer(mMainCallId)

                R.id.tv_show_caller_keyboard -> showCallerKeyboard()

                R.id.tv_hide_keyboard -> hideCallerKeyboard()

                R.id.tv_add_call -> PhoneCallManager.instance.addOneMoreCall(this)

                R.id.cb_hold -> {
                    mSubCallId?.let { subCallId ->
                        mSubCallId = mMainCallId
                        mMainCallId = subCallId
                        initCall(false)
                        return
                    }
                    val isHolding = PhoneCallManager.instance.isHold(mMainCallId)
                    PhoneCallManager.instance.hold(mMainCallId, !isHolding)
                    ActiveReportManager.report(activeEvent = ActiveEvent.PHONE_CALL_HOLD_CLICK, view = v)
                }
                R.id.cb_recording -> {
//                onRecordClick()
                    ToastUtils.showToast("暂未开放")
                    toggleRecordStatus(false)
                }
                R.id.cb_speaker -> {
                    val isSpeakPhoneOn = PhoneCallManager.instance.isSpeakPhoneOn()
                    PhoneCallManager.instance.setSpeakPhoneOn(!isSpeakPhoneOn)
                    ActiveReportManager.report(activeEvent = ActiveEvent.PHONE_CALL_SPEAKER_CLICK, view = v)
                }
                R.id.iv_hand_up -> {
                    PhoneCallManager.instance.disconnect(mMainCallId).also {
                        if (!it) { // 执行挂断失败
                            toggleCallInView(false)
                            checkFinish()
                        }
                    }
                }
                R.id.cb_mute -> {
                    val isMicrophoneMute = PhoneCallManager.instance.isMicrophoneMute()
                    PhoneCallManager.instance.setMicrophoneMute(!isMicrophoneMute)
                    ActiveReportManager.report(activeEvent = ActiveEvent.PHONE_CALL_MUTE_CLICK, view = v)
                }
                else -> {
                }
            }
        }
    }

    private inline fun updateActionState(canAddCall: Boolean? = null) {
        with(PhoneCallManager.instance) {
            cb_speaker.isChecked = isSpeakPhoneOn()
            cb_mute.isChecked = isMicrophoneMute()
            tv_add_call.isEnabled = canAddCall() && canAddCall ?: true
            cb_hold.isEnabled = getCurrentCallSize() === 1
            cb_hold.isChecked = isHold(mMainCallId) && cb_hold.isEnabled
        }
    }

    private fun onRecordClick() {
        if (recordManager?.isRecording == true) {
            recordManager?.stopRecord()
            toggleRecordStatus(false)
        } else {
            AndPermission.with(this)
                    .permission(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onDenied {
                        if (it.contains(Manifest.permission.RECORD_AUDIO)) {
                            Toast.makeText(this,"录音开启失败，请检查录音权限",Toast.LENGTH_SHORT).show()
                        }
                    }
                    .onGranted {
                        recordManager?.startRecord()
                        toggleRecordStatus(recordManager?.isRecording == true)
                    }
                    .start()
        }
    }

    private fun toggleRecordStatus(isRecording: Boolean) {
        cb_recording.isChecked = isRecording
    }

    private inline fun checkFinish() {
        if (!Utils.isEmpty(mSubCallId)) {
            mMainCallId = mSubCallId!!
            mSubCallId = null
            initCall(false)
            return
        }
        finishAndRemoveTask()
    }

    private fun toggleCallInView(showCallInView: Boolean) {
        if (showCallInView) {
            group_caller_hide_keyboard.setVisibilityCompat(View.GONE)
            group_caller_show_keyboard.setVisibilityCompat(View.GONE)
            iv_hand_up.setVisibilityCompat(View.GONE)
            ll_call_in.setVisibilityCompat(View.VISIBLE)
            startWaveAnimation()
        } else {
            group_caller_hide_keyboard.setVisibilityCompat(View.VISIBLE)
            group_caller_show_keyboard.setVisibilityCompat(View.GONE)
            iv_hand_up.setVisibilityCompat(View.VISIBLE)
            ll_call_in.setVisibilityCompat(View.GONE)
            clearWaveAnimation()
        }
    }

    private inline fun showCallerKeyboard() {
        // 显示拨号键盘
        initCallerKeyboardAdapterIfNeeded()
        group_caller_hide_keyboard.setVisibilityCompat(View.GONE)
        caller_header_container.setVisibilityCompat(View.GONE)
        group_caller_show_keyboard.setVisibilityCompat(View.VISIBLE)
        ll_call_in.setVisibilityCompat(View.GONE)
    }

    private inline fun hideCallerKeyboard() {
        // 隐藏拨号键盘
        group_caller_hide_keyboard.setVisibilityCompat(View.VISIBLE)
        caller_header_container.setVisibilityCompat(View.VISIBLE)
        group_caller_show_keyboard.setVisibilityCompat(View.GONE)
        ll_call_in.setVisibilityCompat(View.GONE)
    }

    private inline fun initCallerKeyboardAdapterIfNeeded() {
        if (!Utils.isEmpty(mCallerKeyboardAdapter) && !Utils.isEmpty(rv_caller_keyboard.adapter)) {
            return
        }
        mCallerKeyboardAdapter = CallerKeyboardAdapter().also {
            it.setOnItemClickListener { adapter, _, position ->
                val item = adapter.getItem(position) as CallerKeyboardAdapter.CallerKeyboard
                if (Utils.isEmpty(item)) {
                    return@setOnItemClickListener
                }
                tv_key_input_content.text = "${tv_key_input_content.text}${item.digit}"
                PhoneCallManager.instance.playNumberTone(mMainCallId, item.digit)
            }
        }
        rv_caller_keyboard.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        rv_caller_keyboard.adapter = mCallerKeyboardAdapter
    }

    private fun startWaveAnimation() {
        //缩放动画，以中心从原始放大到1.4倍
        val innerScaleAnimation = ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 800
            repeatCount = Animation.INFINITE
        }
        //渐变动画
        val innerAlphaAnimation = AlphaAnimation(1.0f, 0.5f).apply {
            repeatCount = Animation.INFINITE
        }
        innerAnimationSet.duration = 800
        innerAnimationSet.addAnimation(innerScaleAnimation)
        innerAnimationSet.addAnimation(innerAlphaAnimation)
        ll_call_in.findViewById<View>(R.id.iv_wave_inner)?.startAnimation(innerAnimationSet)

        //缩放动画，以中心从1.4倍放大到1.6倍
        val outerScaleAnimation = ScaleAnimation(1.4f, 1.6f, 1.4f, 1.6f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 800
            repeatCount = Animation.INFINITE
        }
        //渐变动画
        val outerAlphaAnimation = AlphaAnimation(0.5f, 0.1f).apply {
            repeatCount = Animation.INFINITE
        }
        outerAnimationSet.duration = 800
        outerAnimationSet.addAnimation(outerScaleAnimation)
        outerAnimationSet.addAnimation(outerAlphaAnimation)
        ll_call_in.findViewById<View>(R.id.iv_wave_outer)?.startAnimation(outerAnimationSet)
    }

    private fun clearWaveAnimation() {
        innerAnimationSet.cancel()
        innerAnimationSet.reset()

        outerAnimationSet.cancel()
        outerAnimationSet.reset()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode === KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.also {
            mNearFace = it.values[0] < it.sensor?.maximumRange ?: 0f
            if (mNearFace) {
                turnOnProximitySensor()
            } else {
                turnOffProximitySensor()
            }
        }
    }

    private fun turnOnProximitySensor() {
        if (mWakeLock != null && mWakeLock?.isHeld == false) {
            mWakeLock?.acquire()
        }
    }

    private fun turnOffProximitySensor() {
        if (mWakeLock != null && mWakeLock?.isHeld == true) {
            mWakeLock?.release(0)
        }
    }
}