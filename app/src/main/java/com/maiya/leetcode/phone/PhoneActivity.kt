package com.maiya.leetcode.phone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.maiya.leetcode.R
import com.maiya.leetcode.phone.PhoneActivity.Code.REQUEST_CODE_WRITE_SETTINGS
import com.maiya.leetcode.phone.manager.CallListenerService
import com.maiya.leetcode.phone.utils.ActivityStack
import com.maiya.leetcode.phone.utils.CallType
import com.maiya.leetcode.phone.utils.PhoneUtil
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_phone.*


/**
 * 电话相关功能主页
 * @author ymc  2020年4月29日11点11分
 *
 */

@RequiresApi(Build.VERSION_CODES.M)
class PhoneActivity : AppCompatActivity() {

    private var switchCallCheckChangeListener: CompoundButton.OnCheckedChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        ActivityStack.instance.addActivity(this)
        requestPermission()
        initView()
        initPerssmon()
    }

    private fun initPerssmon() {
        //跳转到设置界面
        bt1.setOnClickListener{
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
        //打开系统设置权限
        bt2.setOnClickListener{
            if (!Settings.System.canWrite(applicationContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
            }else Toast.makeText(this,"已同意修改默认系统设置",Toast.LENGTH_SHORT).show()
        }


        bt3.setOnClickListener{

        }


    }

    /**
     * 弹框
     */
    private fun askOverlay(){
        val alertDialog = AlertDialog.Builder(this)
                .setTitle("允许显示悬浮框")
                .setMessage("为了使电话监听服务正常工作，请允许这项权限！")
                .setPositiveButton("去设置") { dialog, _ ->
                    dialog.dismiss()
                    openSettings()
                }.setNegativeButton("稍后再说"){dialog, _ ->
                    dialog.dismiss()
                }
        alertDialog.show()
    }

    /**
     * 打开设置
     */
    private fun openSettings(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            try {
                val context: Context = this
                val clazz: Class<*> = Settings::class.java
                val field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION")
                val intent = Intent(field[null].toString())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "请在权限管理中打开悬浮窗管理权限", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "android 6.0以下", Toast.LENGTH_LONG).show()
        }
    }

    private fun initView() {
        switchCallCheckChangeListener = CompoundButton.OnCheckedChangeListener{ _, isChecked->
            if(isChecked && !Settings.canDrawOverlays(this)){
                askOverlay()
                // 未开启时清除选中状态，同时避免回调
                switch_call_listener.setOnCheckedChangeListener(null)
                switch_call_listener.isChecked = false
                switch_call_listener.setOnCheckedChangeListener(switchCallCheckChangeListener)
                return@OnCheckedChangeListener
            }

            val callListener = Intent(this@PhoneActivity, CallListenerService::class.java)
            if (isChecked) {
                startService(callListener)
                Toast.makeText(this, "电话监听服务已开启", Toast.LENGTH_SHORT).show()
            } else {
                stopService(callListener)
                Toast.makeText(this, "电话监听服务已关闭", Toast.LENGTH_SHORT).show()
            }
        }
        switch_call_listener.setOnCheckedChangeListener(switchCallCheckChangeListener)
        //设置默认电话应用
        switch_default_phone_call.setOnClickListener {
            // 发起将本应用设为默认电话应用的请求，仅支持 Android M 及以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (switch_default_phone_call.isChecked) {
                    val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                            packageName)
                    startActivity(intent)
                } else {
                    // 取消时跳转到默认设置页面
                    startActivity(Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"))
                }
            } else {
                Toast.makeText(this, "Android 6.0 以上才支持修改默认电话应用！", Toast.LENGTH_LONG).show()
                switch_default_phone_call.isChecked = false
            }
        }
        //测试跳转到 呼叫等待界面
        bt.setOnClickListener{
            val intent = Intent(applicationContext, PhoneCallActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Intent.EXTRA_MIME_TYPES, CallType.CALL_IN)
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, "18768880074")
            startActivity(intent)
        }
    }


    override fun onResume() {
        switch_call_listener.isChecked = PhoneUtil.isServiceRunning(CallListenerService::class.java,this)
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_WRITE_SETTINGS ->{
                if (Settings.System.canWrite(applicationContext)) Toast.makeText(this,"获取了修改系统权限",Toast.LENGTH_SHORT).show()
                else Toast.makeText(this,"拒绝了修改系统权限",Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * 请求权限
     */
    private fun requestPermission() {
        AndPermission.with(this)
                .permission(Manifest.permission.CALL_PHONE,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_CALL_LOG,
                        Manifest.permission.WAKE_LOCK,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .onGranted {
                    Toast.makeText(applicationContext,"权限同意", Toast.LENGTH_SHORT).show()
                }.onDenied{
                    if (AndPermission.hasAlwaysDeniedPermission(applicationContext,it)){
                        AndPermission.permissionSetting(applicationContext).execute();
                    }
                    Toast.makeText(applicationContext,"权限拒绝", Toast.LENGTH_SHORT).show()
                }.start()
    }



    object Code{
        const val REQUEST_CODE_WRITE_SETTINGS = 0x001

    }


}
