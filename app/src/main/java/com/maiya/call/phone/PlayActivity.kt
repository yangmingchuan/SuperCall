package com.maiya.call.phone

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private val mp4Url = "http://v3-ppx.ixigua.com/d2445d658173ae705ac90c809113a372/601912da/video/m/2208af78a23ef9b4ccf808f3d5ae853e5aa1166af80d000012cd34460394/?a=1319&br=4808&bt=1202&cd=0%7C0%7C1&ch=0&cr=0&cs=0&cv=1&dr=3&ds=3&er&l=2021020215522901012902420905014359&lr=superb&mime_type=video_mp4&pl=0&qs=0&rc=ajVpdWZkdnYzeDMzZmYzM0ApZjczaTtoPDs7N2lpOmQ6N2dzYTRhLmVpNGBfLS0zMTBzc14xNjAvYzUzLy02NjY0XzY6Yw%3D%3D&vl&vr"
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