package com.maiya.call.phone

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.maiya.call.R
import com.maiya.call.phone.utils.CacheUtils
import com.ymc.ijkplay.IRenderView
import com.ymc.ijkplay.IjkVideoView
import kotlinx.android.synthetic.main.activity_play.*
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * 视频播放模拟界面
 */

class PlayActivity : AppCompatActivity() , IMediaPlayer.OnCompletionListener {
    private var ijkVideoView: IjkVideoView? = null
    private val mp4Url = "http://smallmv.eastday.com/mv/20200601171315831243932_1.mp4"
    private var videoLink = CacheUtils.getString(CacheUtils.SP_FILE_KEY, mp4Url)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        //视频初始化 并默认填充 fill 模式
        ijkVideoView = IjkVideoView(this, IRenderView.AR_ASPECT_FILL_PARENT)
        ijkVideoView?.setOnCompletionListener(this)
        layout_video_container.addView(ijkVideoView)
        val uri = Uri.parse("cache:$videoLink")
        ijkVideoView?.setVideoURI(uri)
    }

    override fun onCompletion(p0: IMediaPlayer?) {
        ijkVideoView?.start()
        ijkVideoView?.keepScreenOn = true
    }
}