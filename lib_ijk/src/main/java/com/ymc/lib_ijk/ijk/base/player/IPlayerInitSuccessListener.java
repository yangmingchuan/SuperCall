package com.ymc.lib_ijk.ijk.base.player;


import com.ymc.lib_ijk.ijk.base.model.IJKModel;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 播放器初始化成果回调
 */
public interface IPlayerInitSuccessListener {
    void onPlayerInitSuccess(IMediaPlayer player, IJKModel model);
}
