package com.maiya.call.phone

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.maiya.call.R
import com.ymc.lib_ijk.ijk.IJKBaseActivityDetail
import com.ymc.lib_ijk.ijk.builder.IJKVideoOptionBuilder
import com.ymc.lib_ijk.ijk.video.StandardIJKVideoPlayer
import kotlinx.android.synthetic.main.activity_play.*

/**
 * 视频播放模拟界面
 */

class PlayActivity : IJKBaseActivityDetail<StandardIJKVideoPlayer>() {

    private val url = "http://mp4.vjshi.com/2013-05-28/2013052815051372.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        resolveNormalVideoUI()
        initVideoBuilderMode()

        vidoe_detail_player.setLockClickListener { view, lock -> }
    }

    override fun getGSYVideoPlayer(): StandardIJKVideoPlayer = vidoe_detail_player

    override fun getGSYVideoOptionBuilder(): IJKVideoOptionBuilder {
        //内置封面可参考SampleCoverVideo

        //内置封面可参考SampleCoverVideo
        val imageView = ImageView(this)
        loadCover(imageView, url)
        return IJKVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle(" ")
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(true) //打开动画
                .setNeedLockFull(true)
                .setSeekRatio(1F)
    }

    override fun clickForFullScreen() {
    }

    /**
     * 是否启动旋转屏幕  true:启动
     */
    override fun getDetailOrientationRotateAuto(): Boolean {
        return false
    }

    /*******************************竖屏全屏开始 */
    override fun initVideo() {
        super.initVideo()
        //重载后实现点击，不横屏
        if (gsyVideoPlayer.fullscreenButton != null) {
            gsyVideoPlayer.fullscreenButton
                    .setOnClickListener { //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                        gsyVideoPlayer.startWindowFullscreen(
                                this@PlayActivity,
                                true,
                                true
                        )
                    }
        }
    }

    //重载后关闭重力旋转
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientationUtils.isEnable = false
    }

    //重载后不做任何事情，实现竖屏全屏
    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
        super.onQuitFullscreen(url, objects)
    }

    /*******************************竖屏全屏结束 */
    private fun loadCover(
            imageView: ImageView,
            url: String
    ) {
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this.applicationContext)
                .setDefaultRequestOptions(
                        RequestOptions()
                                .frame(3000000)
                                .centerCrop()
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                )
                .load(url)
                .into(imageView)
    }

    private fun resolveNormalVideoUI() {
        //增加title
        vidoe_detail_player.titleTextView.visibility = View.GONE
        vidoe_detail_player.backButton.visibility = View.GONE
    }
}