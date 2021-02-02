package com.ymc.lib_ijk.ijk.render.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.ymc.lib_ijk.ijk.listener.IJKVideoShotListener;
import com.ymc.lib_ijk.ijk.listener.IJKVideoShotSaveListener;
import com.ymc.lib_ijk.ijk.render.IJKRenderView;
import com.ymc.lib_ijk.ijk.render.glrender.IJKVideoGLViewBaseRender;
import com.ymc.lib_ijk.ijk.render.view.listener.IIJKSurfaceListener;
import com.ymc.lib_ijk.ijk.utils.FileUtils;
import com.ymc.lib_ijk.ijk.utils.IJKVideoType;
import com.ymc.lib_ijk.ijk.utils.MeasureHelper;

import java.io.File;

/**
 * 用于显示video的，做了横屏与竖屏的匹配，还有需要rotation需求的
 * Created by shuyu on 2016/11/11.
 */

public class IJKTextureView extends TextureView implements TextureView.SurfaceTextureListener, IIJKRenderView, MeasureHelper.MeasureFormVideoParamsListener {

    private IIJKSurfaceListener mIIJKSurfaceListener;

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    private SurfaceTexture mSaveTexture;
    private Surface mSurface;

    public IJKTextureView(Context context) {
        super(context);
        init();
    }

    public IJKTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        measureHelper = new MeasureHelper(this, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
        setMeasuredDimension(measureHelper.getMeasuredWidth(), measureHelper.getMeasuredHeight());
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (IJKVideoType.isMediaCodecTexture()) {
            if (mSaveTexture == null) {
                mSaveTexture = surface;
                mSurface = new Surface(surface);
            } else {
                setSurfaceTexture(mSaveTexture);
            }
            if (mIIJKSurfaceListener != null) {
                mIIJKSurfaceListener.onSurfaceAvailable(mSurface);
            }
        } else {
            mSurface = new Surface(surface);
            if (mIIJKSurfaceListener != null) {
                mIIJKSurfaceListener.onSurfaceAvailable(mSurface);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mIIJKSurfaceListener != null) {
            mIIJKSurfaceListener.onSurfaceSizeChanged(mSurface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        //清空释放
        if (mIIJKSurfaceListener != null) {
            mIIJKSurfaceListener.onSurfaceDestroyed(mSurface);
        }
        if (IJKVideoType.isMediaCodecTexture()) {
            return (mSaveTexture == null);
        } else {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //如果播放的是暂停全屏了
        if (mIIJKSurfaceListener != null) {
            mIIJKSurfaceListener.onSurfaceUpdated(mSurface);
        }
    }

    @Override
    public IIJKSurfaceListener getIGSYSurfaceListener() {
        return mIIJKSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IIJKSurfaceListener surfaceListener) {
        setSurfaceTextureListener(this);
        mIIJKSurfaceListener = surfaceListener;
    }

    @Override
    public int getSizeH() {
        return getHeight();
    }

    @Override
    public int getSizeW() {
        return getWidth();
    }

    /**
     * 暂停时初始化位图
     */
    @Override
    public Bitmap initCover() {
        Bitmap bitmap = Bitmap.createBitmap(
                getSizeW(), getSizeH(), Bitmap.Config.RGB_565);
        return getBitmap(bitmap);

    }

    /**
     * 暂停时初始化位图
     */
    @Override
    public Bitmap initCoverHigh() {
        Bitmap bitmap = Bitmap.createBitmap(
                getSizeW(), getSizeH(), Bitmap.Config.ARGB_8888);
        return getBitmap(bitmap);

    }


    /**
     * 获取截图
     *
     * @param shotHigh 是否需要高清的
     */
    @Override
    public void taskShotPic(IJKVideoShotListener IJKVideoShotListener, boolean shotHigh) {
        if (shotHigh) {
            IJKVideoShotListener.getBitmap(initCoverHigh());
        } else {
            IJKVideoShotListener.getBitmap(initCover());
        }
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    @Override
    public void saveFrame(final File file, final boolean high, final IJKVideoShotSaveListener IJKVideoShotSaveListener) {
        IJKVideoShotListener IJKVideoShotListener = new IJKVideoShotListener() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                if (bitmap == null) {
                    IJKVideoShotSaveListener.result(false, file);
                } else {
                    FileUtils.saveBitmap(bitmap, file);
                    IJKVideoShotSaveListener.result(true, file);
                }
            }
        };
        if (high) {
            IJKVideoShotListener.getBitmap(initCoverHigh());
        } else {
            IJKVideoShotListener.getBitmap(initCover());
        }

    }


    @Override
    public View getRenderView() {
        return this;
    }

    @Override
    public void onRenderResume() {
    }

    @Override
    public void onRenderPause() {
    }

    @Override
    public void releaseRenderAll() {
    }

    @Override
    public void setRenderMode(int mode) {
    }

    @Override
    public void setRenderTransform(Matrix transform) {
        setTransform(transform);
    }

    @Override
    public void setGLRenderer(IJKVideoGLViewBaseRender renderer) {
    }

    @Override
    public void setGLMVPMatrix(float[] MVPMatrix) {
    }

    /**
     * 设置滤镜效果
     */
    @Override
    public void setGLEffectFilter(IJKVideoGLView.ShaderInterface effectFilter) {
    }


    @Override
    public void setVideoParamsListener(MeasureHelper.MeasureFormVideoParamsListener listener) {
        mVideoParamsListener = listener;
    }

    @Override
    public int getCurrentVideoWidth() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getCurrentVideoWidth();
        }
        return 0;
    }

    @Override
    public int getCurrentVideoHeight() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getCurrentVideoHeight();
        }
        return 0;
    }

    @Override
    public int getVideoSarNum() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getVideoSarNum();
        }
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getVideoSarDen();
        }
        return 0;
    }


    /**
     * 添加播放的view
     */
    public static IJKTextureView addTextureView(Context context, ViewGroup textureViewContainer, int rotate,
                                                final IIJKSurfaceListener gsySurfaceListener,
                                                final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        IJKTextureView IJKTextureView = new IJKTextureView(context);
        IJKTextureView.setIGSYSurfaceListener(gsySurfaceListener);
        IJKTextureView.setVideoParamsListener(videoParamsListener);
        IJKTextureView.setRotation(rotate);
        IJKRenderView.addToParent(textureViewContainer, IJKTextureView);
        return IJKTextureView;
    }
}