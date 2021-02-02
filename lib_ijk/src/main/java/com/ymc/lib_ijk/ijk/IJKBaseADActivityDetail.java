package com.ymc.lib_ijk.ijk;

import android.content.res.Configuration;
import android.view.View;

import com.ymc.lib_ijk.ijk.builder.IJKVideoOptionBuilder;
import com.ymc.lib_ijk.ijk.listener.IJKSampleCallBack;
import com.ymc.lib_ijk.ijk.utils.OrientationOption;
import com.ymc.lib_ijk.ijk.utils.OrientationUtils;
import com.ymc.lib_ijk.ijk.video.IJKADVideoPlayer;
import com.ymc.lib_ijk.ijk.video.base.IJKBaseVideoPlayer;
import com.ymc.lib_ijk.ijk.video.base.IJKVideoPlayer;
import com.ymc.lib_ijk.ijk.video.base.IJKVideoView;


/**
 * 详情AD模式播放页面基础类
 * Created by guoshuyu on 2017/9/14.
 */
public abstract class IJKBaseADActivityDetail<T extends IJKBaseVideoPlayer, R extends IJKADVideoPlayer> extends IJKBaseActivityDetail<T> {

    protected OrientationUtils mADOrientationUtils;

    @Override
    public void initVideo() {
        super.initVideo();
        //外部辅助的旋转，帮助全屏
        mADOrientationUtils = new OrientationUtils(this, getGSYADVideoPlayer(), getOrientationOption());
        //初始化不打开外部的旋转
        mADOrientationUtils.setEnable(false);
        if (getGSYADVideoPlayer().getFullscreenButton() != null) {
            getGSYADVideoPlayer().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //直接横屏
                    showADFull();
                    clickForFullScreen();
                }
            });
        }
    }

    /**
     * 选择builder模式
     */
    @Override
    public void initVideoBuilderMode() {
        super.initVideoBuilderMode();
        getGSYADVideoOptionBuilder()
                .setVideoAllCallBack(new IJKSampleCallBack() {

                    @Override
                    public void onStartPrepared(String url, Object... objects) {
                        super.onStartPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        mADOrientationUtils.setEnable(getDetailOrientationRotateAuto());
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        //广告结束，释放
                        getGSYADVideoPlayer().getCurrentPlayer().release();
                        getGSYADVideoPlayer().onVideoReset();
                        getGSYADVideoPlayer().setVisibility(View.GONE);
                        //开始播放原视频，根据是否处于全屏状态判断
                        getGSYVideoPlayer().getCurrentPlayer().startAfterPrepared();
                        if (getGSYADVideoPlayer().getCurrentPlayer().isIfCurrentIsFullscreen()) {
                            getGSYADVideoPlayer().removeFullWindowViewOnly();
                            if (!getGSYVideoPlayer().getCurrentPlayer().isIfCurrentIsFullscreen()) {
                                showFull();
                                getGSYVideoPlayer().setSaveBeforeFullSystemUiVisibility(getGSYADVideoPlayer().getSaveBeforeFullSystemUiVisibility());
                            }
                        }
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        //退出全屏逻辑
                        if (mADOrientationUtils != null) {
                            mADOrientationUtils.backToProtVideo();
                        }
                        if (getGSYVideoPlayer().getCurrentPlayer().isIfCurrentIsFullscreen()) {
                            getGSYVideoPlayer().onBackFullscreen();
                        }
                    }

                })
                .build(getGSYADVideoPlayer());
    }

    /**
     * 正常视频内容的全屏显示
     */
    @Override
    public void showFull() {
        if (orientationUtils.getIsLand() != 1) {
            //直接横屏
            orientationUtils.resolveByClick();
        }
        getGSYVideoPlayer().startWindowFullscreen(this, hideActionBarWhenFull(), hideStatusBarWhenFull());
    }

    @Override
    public void onBackPressed() {
        if (mADOrientationUtils != null) {
            mADOrientationUtils.backToProtVideo();
        }
        if (IJKVideoADManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        IJKVideoADManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IJKVideoADManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IJKVideoADManager.releaseAllVideos();
        if (mADOrientationUtils != null)
            mADOrientationUtils.releaseListener();
    }

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //如果旋转了就全屏
        boolean backUpIsPlay = isPlay;
        if (!isPause && getGSYADVideoPlayer().getVisibility() == View.VISIBLE) {
            if (isADStarted()) {
                isPlay = false;
                getGSYADVideoPlayer().getCurrentPlayer().onConfigurationChanged(this, newConfig, mADOrientationUtils, hideActionBarWhenFull(), hideStatusBarWhenFull());
            }
        }
        super.onConfigurationChanged(newConfig);
        isPlay = backUpIsPlay;
    }


    @Override
    public void onStartPrepared(String url, Object... objects) {
        super.onStartPrepared(url, objects);
    }

    @Override
    public void onPrepared(String url, Object... objects) {
        super.onPrepared(url, objects);
        if (isNeedAdOnStart()) {
            startAdPlay();
        }
    }

    @Override
    public void onEnterFullscreen(String url, Object... objects) {
        super.onEnterFullscreen(url, objects);
        //隐藏调全屏对象的返回按键
        IJKVideoPlayer gsyVideoPlayer = (IJKVideoPlayer) objects[1];
        gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
    }

    @Override
    public void clickForFullScreen() {

    }

    @Override
    public void onComplete(String url, Object... objects) {

    }

    protected boolean isADStarted() {
        return getGSYADVideoPlayer().getCurrentPlayer().getCurrentState() >= 0 &&
                getGSYADVideoPlayer().getCurrentPlayer().getCurrentState() != IJKVideoView.CURRENT_STATE_NORMAL
                && getGSYADVideoPlayer().getCurrentPlayer().getCurrentState() != IJKVideoView.CURRENT_STATE_AUTO_COMPLETE;
    }

    /**
     * 显示播放广告
     */
    public void startAdPlay() {
        getGSYADVideoPlayer().setVisibility(View.VISIBLE);
        getGSYADVideoPlayer().startPlayLogic();
        if (getGSYVideoPlayer().getCurrentPlayer().isIfCurrentIsFullscreen()) {
            showADFull();
            getGSYADVideoPlayer().setSaveBeforeFullSystemUiVisibility(getGSYVideoPlayer().getSaveBeforeFullSystemUiVisibility());
        }
    }

    /**
     * 广告视频的全屏显示
     */
    public void showADFull() {
        if (mADOrientationUtils.getIsLand() != 1) {
            mADOrientationUtils.resolveByClick();
        }
        getGSYADVideoPlayer().startWindowFullscreen(IJKBaseADActivityDetail.this, hideActionBarWhenFull(), hideStatusBarWhenFull());
    }

    /**
     * 可配置旋转 OrientationUtils
     */
    public OrientationOption getOrientationOption() {
        return null;
    }


    public abstract R getGSYADVideoPlayer();

    /**
     * 配置AD播放器
     */
    public abstract IJKVideoOptionBuilder getGSYADVideoOptionBuilder();

    /**
     * 是否播放开始广告
     * 如果返回 false ，setStartAfterPrepared 需要设置为 ture
     */
    public abstract boolean isNeedAdOnStart();
}
