package com.ymc.lib_ijk.ijk;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ymc.lib_ijk.R;
import com.ymc.lib_ijk.ijk.listener.IJKMediaPlayerListener;
import com.ymc.lib_ijk.ijk.utils.CommonUtil;
import com.ymc.lib_ijk.ijk.video.base.IJKVideoPlayer;

import static com.ymc.lib_ijk.ijk.utils.CommonUtil.hideNavKey;


/**
 * 视频管理，单例
 * Created by shuyu on 2016/11/11.
 */

public class IJKVideoManager extends IJKVideoBaseManager {

    public static final int SMALL_ID = R.id.small_id;

    public static final int FULLSCREEN_ID = R.id.full_id;

    public static String TAG = "GSYVideoManager";

    @SuppressLint("StaticFieldLeak")
    private static IJKVideoManager videoManager;


    private IJKVideoManager() {
        init();
    }

    /**
     * 单例管理器
     */
    public static synchronized IJKVideoManager instance() {
        if (videoManager == null) {
            videoManager = new IJKVideoManager();
        }
        return videoManager;
    }

    /**
     * 同步创建一个临时管理器
     */
    public static synchronized IJKVideoManager tmpInstance(IJKMediaPlayerListener listener) {
        IJKVideoManager gsyVideoManager = new IJKVideoManager();
        gsyVideoManager.bufferPoint = videoManager.bufferPoint;
        gsyVideoManager.optionModelList = videoManager.optionModelList;
        gsyVideoManager.playTag = videoManager.playTag;
        gsyVideoManager.currentVideoWidth = videoManager.currentVideoWidth;
        gsyVideoManager.currentVideoHeight = videoManager.currentVideoHeight;
        gsyVideoManager.context = videoManager.context;
        gsyVideoManager.lastState = videoManager.lastState;
        gsyVideoManager.playPosition = videoManager.playPosition;
        gsyVideoManager.timeOut = videoManager.timeOut;
        gsyVideoManager.needMute = videoManager.needMute;
        gsyVideoManager.needTimeOutOther = videoManager.needTimeOutOther;
        gsyVideoManager.setListener(listener);
        return gsyVideoManager;
    }

    /**
     * 替换管理器
     */
    public static synchronized void changeManager(IJKVideoManager gsyVideoManager) {
        videoManager = gsyVideoManager;
    }

    /**
     * 退出全屏，主要用于返回键
     *
     * @return 返回是否全屏
     */
    @SuppressWarnings("ResourceType")
    public static boolean backFromWindowFull(Context context) {
        boolean backFrom = false;
        ViewGroup vp = (ViewGroup) (CommonUtil.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(FULLSCREEN_ID);
        if (oldF != null) {
            backFrom = true;
            hideNavKey(context);
            if (IJKVideoManager.instance().lastListener() != null) {
                IJKVideoManager.instance().lastListener().onBackFullscreen();
            }
        }
        return backFrom;
    }

    /**
     * 页面销毁了记得调用是否所有的video
     */
    public static void releaseAllVideos() {
        if (IJKVideoManager.instance().listener() != null) {
            IJKVideoManager.instance().listener().onCompletion();
        }
        IJKVideoManager.instance().releaseMediaPlayer();
    }


    /**
     * 暂停播放
     */
    public static void onPause() {
        if (IJKVideoManager.instance().listener() != null) {
            IJKVideoManager.instance().listener().onVideoPause();
        }
    }

    /**
     * 恢复播放
     */
    public static void onResume() {
        if (IJKVideoManager.instance().listener() != null) {
            IJKVideoManager.instance().listener().onVideoResume();
        }
    }


    /**
     * 恢复暂停状态
     *
     * @param seek 是否产生seek动作,直播设置为false
     */
    public static void onResume(boolean seek) {
        if (IJKVideoManager.instance().listener() != null) {
            IJKVideoManager.instance().listener().onVideoResume(seek);
        }
    }

    /**
     * 当前是否全屏状态
     *
     * @return 当前是否全屏状态， true代表是。
     */
    @SuppressWarnings("ResourceType")
    public static boolean isFullState(Activity activity) {
        ViewGroup vp = (ViewGroup) (CommonUtil.scanForActivity(activity)).findViewById(Window.ID_ANDROID_CONTENT);
        final View full = vp.findViewById(FULLSCREEN_ID);
        IJKVideoPlayer gsyVideoPlayer = null;
        if (full != null) {
            gsyVideoPlayer = (IJKVideoPlayer) full;
        }
        return gsyVideoPlayer != null;
    }

}