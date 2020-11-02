package com.maiya.leetcode.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maiya.leetcode.R
import com.maiya.leetcode.hit.FixDexUtil
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.File


/**
 * 欢迎界面
 */

class SplashActivity : AppCompatActivity() {

    private val handler = Handler{
        when(it.what){
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestPermission()
    }

    private fun requestPermission() {
        AndPermission.with(this)
                .permission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ANSWER_PHONE_CALLS)
                .onGranted {
                    Toast.makeText(applicationContext,"权限同意",Toast.LENGTH_SHORT).show()
                    init()
                }.onDenied{
                    if (AndPermission.hasAlwaysDeniedPermission(applicationContext,it)){
                        AndPermission.permissionSetting(applicationContext).execute();
                    }
                    Toast.makeText(applicationContext,"权限拒绝",Toast.LENGTH_SHORT).show()
                }.start()
    }

    /**
     * 热修复判断
     */
    private fun init() {
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        val fileDir = if(externalStorageDirectory !=null) File(externalStorageDirectory,"007") else
            File(filesDir, FixDexUtil.DEX_DIR)
        if(!fileDir.exists()){
            fileDir.mkdirs()
        }
        if(FixDexUtil.isGoingToFix(this)){
            FixDexUtil.loadFixedDex(this, Environment.getExternalStorageDirectory());
            tv.text = "热更新修复"
        }else{
            tv.text = "不需要修复..."
        }
        handler.postDelayed(Runnable {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        },3000)
    }
}
