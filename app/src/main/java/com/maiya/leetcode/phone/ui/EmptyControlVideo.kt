package com.maiya.leetcode.phone.ui

import android.content.Context
import android.util.AttributeSet
import com.maiya.leetcode.R
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

/**
 * 无操作 播放器
 * 
 * Author : ymc
 * Date   : 2020/5/7  15:28
 * Class  : EmptyControlVideo
 */

class EmptyControlVideo : StandardGSYVideoPlayer {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun getLayoutId(): Int {
        return R.layout.empty_control_video
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false
        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false
        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false
    }

    override fun touchDoubleUp() { //super.touchDoubleUp();
    }
}