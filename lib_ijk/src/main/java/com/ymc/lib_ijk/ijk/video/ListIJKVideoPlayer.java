package com.ymc.lib_ijk.ijk.video;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ymc.lib_ijk.ijk.base.ENDownloadView;
import com.ymc.lib_ijk.ijk.model.GSYVideoModel;
import com.ymc.lib_ijk.ijk.video.base.IJKBaseVideoPlayer;
import com.ymc.lib_ijk.ijk.video.base.IJKVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 列表播放支持
 * Created by shuyu on 2016/12/20.
 */

public class ListIJKVideoPlayer extends StandardIJKVideoPlayer {

    protected List<GSYVideoModel> mUriList = new ArrayList<>();
    protected int mPlayPosition;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public ListIJKVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public ListIJKVideoPlayer(Context context) {
        super(context);
    }

    public ListIJKVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param position      需要播放的位置
     * @param cacheWithPlay 是否边播边缓存
     * @return
     */
    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position) {
        return setUp(url, cacheWithPlay, position, null, new HashMap<String, String>());
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @return
     */
    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath) {
        return setUp(url, cacheWithPlay, position, cachePath, new HashMap<String, String>());
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   http header
     * @return
     */
    public boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData) {
        return setUp(url, cacheWithPlay, position, cachePath, mapHeadData, true);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   http header
     * @param changeState   切换的时候释放surface
     * @return
     */
    protected boolean setUp(List<GSYVideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData, boolean changeState) {
        mUriList = url;
        mPlayPosition = position;
        mMapHeadData = mapHeadData;
        GSYVideoModel gsyVideoModel = url.get(position);
        boolean set = setUp(gsyVideoModel.getUrl(), cacheWithPlay, cachePath, gsyVideoModel.getTitle(), changeState);
        if (!TextUtils.isEmpty(gsyVideoModel.getTitle()) && mTitleTextView != null ) {
            mTitleTextView.setText(gsyVideoModel.getTitle());
        }
        return set;
    }


    @Override
    protected void cloneParams(IJKBaseVideoPlayer from, IJKBaseVideoPlayer to) {
        super.cloneParams(from, to);
        ListIJKVideoPlayer sf = (ListIJKVideoPlayer) from;
        ListIJKVideoPlayer st = (ListIJKVideoPlayer) to;
        st.mPlayPosition = sf.mPlayPosition;
        st.mUriList = sf.mUriList;
    }

    @Override
    public IJKBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        IJKBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        if (gsyBaseVideoPlayer != null) {
            ListIJKVideoPlayer listGSYVideoPlayer = (ListIJKVideoPlayer) gsyBaseVideoPlayer;
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle()) && mTitleTextView != null) {
                listGSYVideoPlayer.mTitleTextView.setText(gsyVideoModel.getTitle());
            }
        }
        return gsyBaseVideoPlayer;
    }

    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, IJKVideoPlayer gsyVideoPlayer) {
        if (gsyVideoPlayer != null) {
            ListIJKVideoPlayer listGSYVideoPlayer = (ListIJKVideoPlayer) gsyVideoPlayer;
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle()) && mTitleTextView != null) {
                mTitleTextView.setText(gsyVideoModel.getTitle());
            }
        }
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
    }

    @Override
    public void onCompletion() {
        releaseNetWorkState();
        if (mPlayPosition < (mUriList.size())) {
            return;
        }
        super.onCompletion();
    }

    @Override
    public void onAutoCompletion() {
        if (playNext()) {
            return;
        }
        super.onAutoCompletion();
    }


    /**
     * 开始状态视频播放，prepare时不执行  addTextureView();
     */
    @Override
    protected void prepareVideo() {
        super.prepareVideo();
        if (mHadPlay && mPlayPosition < (mUriList.size())) {
            setViewShowState(mLoadingProgressBar, VISIBLE);
            if (mLoadingProgressBar instanceof ENDownloadView) {
                ((ENDownloadView) mLoadingProgressBar).start();
            }
        }
    }


    @Override
    public void onPrepared() {
        super.onPrepared();
    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        if (mHadPlay && mPlayPosition < (mUriList.size())) {
            setViewShowState(mThumbImageViewLayout, GONE);
            setViewShowState(mTopContainer, INVISIBLE);
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, GONE);
            setViewShowState(mLoadingProgressBar, VISIBLE);
            setViewShowState(mBottomProgressBar, INVISIBLE);
            setViewShowState(mLockScreen, GONE);
            if (mLoadingProgressBar instanceof ENDownloadView) {
                ((ENDownloadView) mLoadingProgressBar).start();
            }
        }
    }

    /**
     * 播放下一集
     *
     * @return true表示还有下一集
     */
    public boolean playNext() {
        if (mPlayPosition < (mUriList.size() - 1)) {
            mPlayPosition += 1;
            GSYVideoModel gsyVideoModel = mUriList.get(mPlayPosition);
            mSaveChangeViewTIme = 0;
            setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
            if (!TextUtils.isEmpty(gsyVideoModel.getTitle()) && mTitleTextView != null) {
                mTitleTextView.setText(gsyVideoModel.getTitle());
            }
            startPlayLogic();
            return true;
        }
        return false;
    }
}
