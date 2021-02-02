package com.ymc.lib_ijk;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ymc.lib_ijk.ijk.utils.IJKVideoType;
import com.ymc.lib_ijk.ijk.video.StandardIJKVideoPlayer;
import com.ymc.lib_ijk.ijk.video.base.IJKBaseVideoPlayer;
import com.ymc.lib_ijk.ijk.video.base.IJKVideoPlayer;

/**
 * Created by shuyu on 2016/12/7.
 * 注意
 * 这个播放器的demo配置切换到全屏播放器
 * 这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
 */

public class SampleControlVideo extends StandardIJKVideoPlayer {

    private TextView mMoreScale;

    private TextView mChangeRotate;

    private TextView switchSpeed;

    //记住切换数据源类型
    private int mType = 0;

    private int mTransformSize = 0;

    //数据源
    private int mSourcePosition = 0;
    private float speed = 1;
    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public SampleControlVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SampleControlVideo(Context context) {
        super(context);
    }

    public SampleControlVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }

    private void initView() {
        mMoreScale = (TextView) findViewById(R.id.moreScale);
        mChangeRotate = (TextView) findViewById(R.id.change_rotate);
        switchSpeed = (TextView) findViewById(R.id.switchSpeed);

        //切换清晰度
        mMoreScale.setOnClickListener(v -> {
            if (!mHadPlay) {
                return;
            }
            if (mType == 0) {
                mType = 1;
            } else if (mType == 1) {
                mType = 2;
            } else if (mType == 2) {
                mType = 3;
            } else if (mType == 3) {
                mType = 4;
            } else if (mType == 4) {
                mType = 0;
            }
            resolveTypeUI();
        });

        //旋转播放角度
        mChangeRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHadPlay) {
                    return;
                }
                if ((mTextureView.getRotation() - mRotate) == 270) {
                    mTextureView.setRotation(mRotate);
                    mTextureView.requestLayout();
                } else {
                    mTextureView.setRotation(mTextureView.getRotation() + 90);
                    mTextureView.requestLayout();
                }
            }
        });

        switchSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speed == 1.0) {
                    speed = 1.5f;
                } else if (speed == 1.5f) {
                    speed = 2.0f;
                } else if (speed == 2) {
                    speed = 1.0f;
                }
                switchSpeed.setText(" x" + speed);
                setSpeedPlaying(speed, true);
            }
        });

    }

    @Override
    public void onSurfaceSizeChanged(Surface surface, int width, int height) {
        super.onSurfaceSizeChanged(surface, width, height);
        resolveTransform();
    }

    /**
     * 处理显示逻辑
     */
    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        resolveRotateUI();
        resolveTransform();
    }

    /**
     * 处理镜像旋转
     * 注意，暂停时
     */
    protected void resolveTransform() {
        switch (mTransformSize) {
            case 1: {
                Matrix transform = new Matrix();
                transform.setScale(-1, 1, mTextureView.getWidth() / 2, 0);
                mTextureView.setTransform(transform);
//                mChangeTransform.setText("左右镜像");
                mTextureView.invalidate();
            }
            break;
            case 2: {
                Matrix transform = new Matrix();
                transform.setScale(1, -1, 0, mTextureView.getHeight() / 2);
                mTextureView.setTransform(transform);
//                mChangeTransform.setText("上下镜像");
                mTextureView.invalidate();
            }
            break;
            case 0: {
                Matrix transform = new Matrix();
                transform.setScale(1, 1, mTextureView.getWidth() / 2, 0);
                mTextureView.setTransform(transform);
//                mChangeTransform.setText("旋转镜像");
                mTextureView.invalidate();
            }
            break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.sample_video;
    }


    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    @Override
    public IJKBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        SampleControlVideo sampleVideo = (SampleControlVideo) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mSourcePosition = mSourcePosition;
        sampleVideo.mType = mType;
        sampleVideo.mTransformSize = mTransformSize;
        //sampleVideo.resolveTransform();
        sampleVideo.resolveTypeUI();
        //sampleVideo.resolveRotateUI();
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        return sampleVideo;
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, IJKVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            SampleControlVideo sampleVideo = (SampleControlVideo) gsyVideoPlayer;
            mSourcePosition = sampleVideo.mSourcePosition;
            mType = sampleVideo.mType;
            mTransformSize = sampleVideo.mTransformSize;
            resolveTypeUI();
        }
    }

    /**
     * 旋转逻辑
     */
    private void resolveRotateUI() {
        if (!mHadPlay) {
            return;
        }
        mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();
    }

    /**
     * 显示比例
     * 注意，IJKVideoType.setShowType是全局静态生效，除非重启APP。
     */
    private void resolveTypeUI() {
        if (!mHadPlay) {
            return;
        }
        if (mType == 1) {
            mMoreScale.setText("16:9");
            IJKVideoType.setShowType(IJKVideoType.SCREEN_TYPE_16_9);
        } else if (mType == 2) {
            mMoreScale.setText("4:3");
            IJKVideoType.setShowType(IJKVideoType.SCREEN_TYPE_4_3);
        } else if (mType == 3) {
            mMoreScale.setText("全屏");
            IJKVideoType.setShowType(IJKVideoType.SCREEN_TYPE_FULL);
        } else if (mType == 4) {
            mMoreScale.setText("拉伸全屏");
            IJKVideoType.setShowType(IJKVideoType.SCREEN_MATCH_FULL);
        } else if (mType == 0) {
            mMoreScale.setText("默认比例");
            IJKVideoType.setShowType(IJKVideoType.SCREEN_TYPE_DEFAULT);
        }
        changeTextureViewShowType();
        if (mTextureView != null)
            mTextureView.requestLayout();
    }


}
