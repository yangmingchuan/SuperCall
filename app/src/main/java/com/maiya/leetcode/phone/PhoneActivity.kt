package com.maiya.leetcode.phone

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.maiya.leetcode.R
import com.maiya.leetcode.phone.PhoneActivity.Code.REQUEST_CODE_WRITE_SETTINGS
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_phone.*


/**
 * 电话相关功能主页
 * @author ymc  2020年4月29日11点11分
 *
 */

@RequiresApi(Build.VERSION_CODES.M)
class PhoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        requestPermission()
        initView()
        initPersimmon()
    }

    private fun initView() {
    }

    private fun initPersimmon() {
        bt3.setOnClickListener {
            val intent = Intent(this, PhoneListActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_WRITE_SETTINGS -> {
                if (Settings.System.canWrite(applicationContext)) Toast.makeText(this, "获取了修改系统权限", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "拒绝了修改系统权限", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * 请求权限
     */
    private fun requestPermission() {
        AndPermission.with(this)
                .permission(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.MODIFY_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WAKE_LOCK
                )
                .onGranted {
                    Toast.makeText(applicationContext, "权限同意", Toast.LENGTH_SHORT).show()
                }.onDenied {
                    if (AndPermission.hasAlwaysDeniedPermission(applicationContext, it)) {
                        //AndPermission.permissionSetting(applicationContext).execute();
                    }
                    Toast.makeText(applicationContext, "权限拒绝", Toast.LENGTH_SHORT).show()
                }.start()

    }


    object Code {
        const val REQUEST_CODE_WRITE_SETTINGS = 0x001

    }


}
