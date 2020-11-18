package com.maiya.call.dialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.maiya.call.R
import com.maiya.call.phone.manager.CallerShowManager
import com.maiya.call.phone.manager.CallerShowPermissionManager

/**
 * 铃声权限弹框
 *
 * Author : ymc
 * Class  : RingPermissionDialog
 */

class RingPermissionDialog(ctx: Context) : Dialog(ctx, R.style.PromptDialog) {

    private lateinit var btPermission: Button
    private lateinit var tvTitle: TextView
    private lateinit var tvContent: TextView
    private lateinit var ivClose: ImageView

    private var mPermissionGroup: Array<String>
    private var onPermissionListener: OnPermissionListener? = null
    private var onOKListener: OnPermissionOkListener? = null

    init {
        initView()
        initListener()
        mPermissionGroup = PERMISSION_VIDEO_RING
    }

    private fun initView() {
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_ring_permission, null).also {
            btPermission = it.findViewById(R.id.bt_setting_permission)
            tvTitle = it.findViewById(R.id.tv_permission_title)
            tvContent = it.findViewById(R.id.tv_permission_content)
            ivClose = it.findViewById(R.id.iv_close)

            updateRingPerContent()
        })
        window?.apply {
            val lp = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            attributes = lp
        }
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    private fun initListener() {
        btPermission.setOnClickListener {
            val hasPer = CallerShowPermissionManager.instance.setRingPermission(context)
            if (hasPer) {
                onOKListener?.onOK()
            }
        }
        ivClose.setOnClickListener {
            callDenied()
        }
    }

    fun isPermissionAllGranted(): Boolean {
        val content = CallerShowPermissionManager.instance.getPerToContent(context)
        return content.isEmpty()
    }

    /**
     * 更新dialog 内容
     */
    fun updateRingPerContent(): String {
        val content = CallerShowPermissionManager.instance.getPerToContent(context)
        tvContent.text = content
        val arr = content.split("、")
        tvTitle.text = "轻松 ${arr.size - 1} 步设置铃声"
        return content
    }

    fun getRingVideoPermission(activity: Activity, listener: CallerShowManager.OnPerManagerListener?) {
        CallerShowManager.instance.setRingShow(activity, object : CallerShowManager.OnPerManagerListener {
            override fun onGranted() {
                listener?.onGranted()
            }

            override fun onDenied() {
                listener?.onDenied()
            }
        })
    }

    fun getRingPermission(activity: Activity, listener: CallerShowManager.OnPerManagerListener?) {
        CallerShowManager.instance.setRingShow(activity, object : CallerShowManager.OnPerManagerListener {
            override fun onGranted() {
                listener?.onGranted()
                callGranted()
            }

            override fun onDenied() {
                listener?.onDenied()
                callDenied()
            }

        })
    }


    private fun callGranted() {
        onPermissionListener?.onGranted(this)?.also {
            if (!it && isShowing) {
                dismiss()
            }
        } ?: dismiss()
    }

    private fun callDenied() {
        onPermissionListener?.onDenied(this)?.also {
            if (!it) {
                dismiss()
            }
        } ?: dismiss()
    }

    fun setOnPermissionListener(listener: OnPermissionListener?): RingPermissionDialog {
        onPermissionListener = listener
        return this
    }

    interface OnPermissionListener {
        fun onGranted(dialog: RingPermissionDialog): Boolean

        fun onDenied(dialog: RingPermissionDialog): Boolean
    }

    fun setOnPermissionOkListener(listener: OnPermissionOkListener?): RingPermissionDialog {
        onOKListener = listener
        return this
    }

    interface OnPermissionOkListener {
        fun onOK()
    }

    companion object {

        /**
         * 视频铃声需要的必要权限
         */
        @JvmStatic
        var PERMISSION_VIDEO_RING = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ANSWER_PHONE_CALLS

        )

    }

}