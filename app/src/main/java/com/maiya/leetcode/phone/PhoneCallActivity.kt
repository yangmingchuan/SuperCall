package com.maiya.leetcode.phone

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.maiya.leetcode.R
import com.maiya.leetcode.phone.manager.PhoneCallManager
import com.maiya.leetcode.phone.ui.EmptyControlVideo
import com.maiya.leetcode.phone.utils.ActivityStack
import com.maiya.leetcode.phone.utils.CallType
import com.maiya.leetcode.phone.utils.PhoneUtil
import kotlinx.android.synthetic.main.activity_phone_call.*
import java.util.*


/**
 * 电话拨打界面
 * @author ymc 2020年4月29日
 */

@RequiresApi(Build.VERSION_CODES.M)
class PhoneCallActivity : AppCompatActivity(), View.OnClickListener {
    // 工具类 类型
    private lateinit var phoneCallManager: PhoneCallManager
    private lateinit var callType: CallType

    // 电话号码 事件
    private var phoneNumber: String? =null
    private var onGoingCallTimer: Timer? = null
    private var callingTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_call)
        ActivityStack().addActivity(this)
        Log.e("PhoneCallActivity","ActivityStack.addActivity")
        initData()
        initView()
    }

    private fun initData() {
        phoneCallManager = PhoneCallManager(this)
        onGoingCallTimer = Timer()
        if (intent != null) {
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            callType = intent.getSerializableExtra(Intent.EXTRA_MIME_TYPES) as CallType
        }
    }

    private fun initView() {
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.decorView.systemUiVisibility = uiOptions
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        tv_call_number.text = PhoneUtil.formatPhoneNumber(phoneNumber!!)
        tv_phone_pick_up.setOnClickListener(this)
        tv_phone_hang_up.setOnClickListener(this)

        // 打进的电话
        if (callType === CallType.CALL_IN) {
            tv_call_number_label.text = "来电号码"
            tv_phone_pick_up.visibility = View.VISIBLE
        } else if (callType === CallType.CALL_OUT) {
            tv_call_number_label.text = "呼叫号码"
            tv_phone_pick_up.visibility = View.GONE
            phoneCallManager.openSpeaker()
        }
        wakeUpAndUnlock()
        initMedia()
    }

    private fun initMedia() {
        var videoPlayer = findViewById<EmptyControlVideo>(R.id.videoPlayer)
        val source1 = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
        videoPlayer.setUp(source1, true, "")
        videoPlayer.startPlayLogic()
    }


    private fun wakeUpAndUnlock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager ?: return
        val screenOn = pm.isScreenOn
        if (!screenOn) {
            val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, packageName.toString() + "TAG")
            wl.acquire(10000)
            wl.release()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                    ?: return
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.tv_phone_pick_up) {
            phoneCallManager.answer()
            tv_phone_pick_up.visibility = View.GONE
            tv_phone_calling_time.visibility = View.VISIBLE
            onGoingCallTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        callingTime++
                        tv_phone_calling_time.text = "通话中：" + PhoneUtil.getCallingTime(callingTime)
                    }
                }
            }, 0, 1000)
        } else if (v.id == R.id.tv_phone_hang_up) {
            phoneCallManager.disconnect()
            onGoingCallTimer!!.cancel()
            callingTime = 0
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityStack().finishActivity(this)
        Log.e("PhoneCallActivity","ActivityStack.finishActivity")
        phoneCallManager.destroy()
    }
}
