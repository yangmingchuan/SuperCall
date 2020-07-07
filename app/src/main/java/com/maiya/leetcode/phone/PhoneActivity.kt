package com.maiya.leetcode.phone

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.maiya.leetcode.R
import com.maiya.leetcode.dialog.RingPermissionDialog
import com.maiya.leetcode.phone.manager.CallerShowManager
import com.maiya.leetcode.phone.utils.CacheUtils
import com.maiya.leetcode.util.LogUtils
import com.maiya.leetcode.util.file.UpdateDownloadUtils
import com.maiya.leetcode.util.file.download.FileDownloadCallback
import com.maiya.leetcode.util.file.download.FileDownloadRequest
import com.maiya.leetcode.util.file.download.FileDownloadTask
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_phone.*
import java.io.File


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
            val dialog = RingPermissionDialog(this)
            dialog.getRingVideoPermission(this,object :CallerShowManager.OnPerManagerListener{
                override fun onGranted() {
                    dialog.show()
                    updateRingPerContent(dialog)
                }

                override fun onDenied() {
                    Toast.makeText(applicationContext, "请至权限管理同意权限，才能设置视频铃声", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    fun updateRingPerContent(ringPerDialog: RingPermissionDialog?) {
        ringPerDialog?.let {
            if (it.isShowing) {
                val content: String = it.updateRingPerContent()
                if (content!=null) {
                    it.dismiss()
                    downloadFile()
                }
            }
        }
    }

    private fun downloadFile(url: String?) {
        val filePath: String = UpdateDownloadUtils.getApkUpdateFileName(url)
        val fileu = filePath.split(".apk").toTypedArray()[0] + ".mp4"
        val file = File(fileu)
        FileDownloadRequest.download(url, file, FileDownloadTask.DOWN_LOAD_NO_FILTER_TYPE, object : FileDownloadCallback() {
            private var currentProgress = 0
            private var currentTime: Long = 0
            override fun onStart() {
                super.onStart()
                LogUtils.e("视频下载开始")
                getView().downloadVideoStart()
            }

            override fun onProgress(progress: Int, networkSpeed: Long) {
                if (currentProgress != progress && System.currentTimeMillis() - currentTime > 100 && progress != 100) { //限制刷新间隔最少100ms
                    val msg = Message.obtain()
                    msg.what = UpdateConfig.MSG_UPDATE
                    msg.arg1 = progress
                    currentTime = System.currentTimeMillis()
                }
                currentProgress = progress
                LogUtils.e("视频下载中 进度：$currentProgress")
                getView().downloadingVideo(currentProgress)
            }

            override fun onDone() {
                super.onDone()
                LogUtils.e("视频下载完成")
                getView().downloadVideoEnd()
                CacheUtils.putString(CacheUtils.SP_FILE_KEY, fileu)
                CacheUtils.putString(IRingtoneManager.KEY_SET_RING_TYPE, IRingtoneManager.TYPE_RING_VIDEO)
                if (Utils.isEmpty(mRingVideoEntity)) {
                    return
                }
                TaskManager.INSTANCE.reportAchievementDot(getActivity(), TaskManager.ACHIEVEMENT_DOT_TYPE_VIDEO_RING
                        , mRingVideoEntity.getId(), mRingVideoEntity.getNm().toString() + "-" + mRingVideoEntity.getUname())
            }

            override fun onFailure() {
                super.onFailure()
                getView().downloadVideoErr()
            }
        })
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
