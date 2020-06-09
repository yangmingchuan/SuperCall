package com.maiya.leetcode.phone

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.maiya.leetcode.MApplication
import com.maiya.leetcode.R
import com.maiya.leetcode.dialog.RingPermissionDialog
import com.maiya.leetcode.phone.manager.CallerShowManager
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_phone.*


/**
 * 电话相关功能主页
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
        tv_set_ring.setOnClickListener{
            var dialog = RingPermissionDialog(this)
            dialog.getRingVideoPermission(this,object :CallerShowManager.OnPerManagerListener{
                override fun onGranted() {
                    Toast.makeText(MApplication.instance,"请至权限管理同意权限，才能设置视频铃声.",Toast.LENGTH_SHORT).show()
                }

                override fun onDenied() {
                    dialog.show()
                }

            })
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


}
