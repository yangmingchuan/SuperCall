package com.maiya.leetcode.phone.manager

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.maiya.leetcode.R
import com.maiya.leetcode.phone.PhoneCallActivity
import com.maiya.leetcode.phone.receiver.AutoStartReceiver
import com.maiya.leetcode.phone.utils.ActivityStack
import com.maiya.leetcode.phone.utils.PhoneUtil

/**
 * 电话监听
 * Author : ymc
 * Date   : 2020/4/29  13:55
 * Class  : CallListenerService
 */
class CallListenerService : Service() {
    // 相关view
    private lateinit var phoneCallView: View
    private lateinit var tvCallNumber: TextView
    private lateinit var btnOpenApp: TextView
    //
    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var phoneStateListener: PhoneStateListener
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callNumber: String
    // 电话状态判断
    private var hasShown = false
    private var isCallingIn = false

    override fun onCreate() {
        super.onCreate()
        initPhoneStateListener()
        initPhoneCallView()
    }

    private fun initPhoneStateListener() {
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                super.onCallStateChanged(state, incomingNumber)
                callNumber = incomingNumber
                when (state) {
                    TelephonyManager.CALL_STATE_IDLE -> {
                        Toast.makeText(applicationContext,"通话空闲",Toast.LENGTH_SHORT).show()
                        ActivityStack().finishActivity(PhoneCallActivity::class.java)
                        dismiss()
                    }
                    TelephonyManager.CALL_STATE_RINGING -> {
                        isCallingIn = true
                        Toast.makeText(applicationContext,"摘机状态，接听或者拨打",Toast.LENGTH_SHORT).show()
                        //updateUI()
                        //show()
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        Toast.makeText(applicationContext,"响铃:来电号码:" + incomingNumber,Toast.LENGTH_SHORT).show()
                        //updateUI()
                        //show()
                    }
                    else -> {
                    }
                }
            }
        }
        // 设置来电监听器
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)

    }

    /**
     * 显示顶级弹框展示通话信息
     */
    private fun show() {
        if (!hasShown) {
            windowManager.addView(phoneCallView, params)
            hasShown = true
        }
    }

    /**
     * 取消显示
     */
    private fun dismiss() {
        if (hasShown) {
            windowManager.removeView(phoneCallView)
            isCallingIn = false
            hasShown = false
        }
    }

    private fun initPhoneCallView() {
        windowManager = applicationContext
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = windowManager.defaultDisplay.width

        params = WindowManager.LayoutParams()
        params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        params.width = width
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // 设置图片格式，效果为背景透明
        params.format = PixelFormat.TRANSLUCENT
        // 设置 Window flag 为系统级弹框 | 覆盖表层
        params.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        // 不可聚集（不响应返回键）| 全屏
        params.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_FULLSCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        // API 19 以上则还可以开启透明状态栏与导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        }
        val interceptorLayout: FrameLayout = object : FrameLayout(this) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                        return true
                    }
                }
                return super.dispatchKeyEvent(event)
            }
        }

        phoneCallView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_phone_call, interceptorLayout)
        tvCallNumber = phoneCallView.findViewById<TextView>(R.id.tv_call_number)
        btnOpenApp = phoneCallView.findViewById<Button>(R.id.btn_open_app)
        btnOpenApp.setOnClickListener {
            dismiss()
            val intent = Intent(applicationContext, PhoneCallActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this@CallListenerService.startActivity(intent)
        }
    }

    /**
     * 更新UI
     */
    fun updateUI() {
        tvCallNumber.text = PhoneUtil.formatPhoneNumber(callNumber)
        val callTypeDrawable: Int = if (isCallingIn) R.drawable.ic_phone_call_in else R.drawable.ic_phone_call_out
        tvCallNumber.setCompoundDrawablesWithIntrinsicBounds(null, null,
                resources.getDrawable(callTypeDrawable), null)
    }



    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        // 重新拉起
        sendBroadcast(Intent(AutoStartReceiver.AUTO_START_RECEIVER))
        super.onDestroy()
    }

}