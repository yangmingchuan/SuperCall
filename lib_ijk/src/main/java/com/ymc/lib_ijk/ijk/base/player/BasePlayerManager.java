package com.ymc.lib_ijk.ijk.base.player;


import com.ymc.lib_ijk.ijk.base.model.IJKModel;

/**
 * 播放器差异管理接口
 Created by guoshuyu on 2018/1/11.
 */

public abstract class BasePlayerManager implements IPlayerManager {

    protected IPlayerInitSuccessListener mPlayerInitSuccessListener;

    public IPlayerInitSuccessListener getPlayerPreparedSuccessListener() {
        return mPlayerInitSuccessListener;
    }

    public void setPlayerInitSuccessListener(IPlayerInitSuccessListener listener) {
        this.mPlayerInitSuccessListener = listener;
    }

    protected void initSuccess(IJKModel IJKModel) {
        if (mPlayerInitSuccessListener != null) {
            mPlayerInitSuccessListener.onPlayerInitSuccess(getMediaPlayer(), IJKModel);
        }
    }
}
