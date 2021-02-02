package com.ymc.lib_ijk.ijk.video.base;

import android.content.Context;
import android.util.AttributeSet;

import com.ymc.lib_ijk.ijk.IJKVideoManager;


/**
 * 兼容的空View，目前用于 GSYVideoManager的设置
 * Created by shuyu on 2016/11/11.
 */

public abstract class IJKVideoPlayer extends IJKBaseVideoPlayer {

    public IJKVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public IJKVideoPlayer(Context context) {
        super(context);
    }

    public IJKVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IJKVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*******************************下面方法为管理器和播放控件交互的方法****************************************/

    @Override
    public GSYVideoViewBridge getGSYVideoManager() {
        IJKVideoManager.instance().initContext(getContext().getApplicationContext());
        return IJKVideoManager.instance();
    }

    @Override
    protected boolean backFromFull(Context context) {
        return IJKVideoManager.backFromWindowFull(context);
    }

    @Override
    protected void releaseVideos() {
        IJKVideoManager.releaseAllVideos();
    }

    @Override
    protected int getFullId() {
        return IJKVideoManager.FULLSCREEN_ID;
    }

    @Override
    protected int getSmallId() {
        return IJKVideoManager.SMALL_ID;
    }

}