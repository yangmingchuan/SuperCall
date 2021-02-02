package com.ymc.lib_ijk.ijk.render.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.ymc.lib_ijk.ijk.listener.IJKVideoShotListener;
import com.ymc.lib_ijk.ijk.listener.IJKVideoShotSaveListener;
import com.ymc.lib_ijk.ijk.render.IJKRenderView;
import com.ymc.lib_ijk.ijk.render.glrender.IJKVideoGLViewBaseRender;
import com.ymc.lib_ijk.ijk.render.view.listener.IIJKSurfaceListener;
import com.ymc.lib_ijk.ijk.utils.MeasureHelper;

import java.io.File;

/**
 * SurfaceView
 * Created by guoshuyu on 2017/8/26.
 */

public class IJKSurfaceView extends SurfaceView implements SurfaceHolder.Callback2, IIJKRenderView, MeasureHelper.MeasureFormVideoParamsListener {

    private IIJKSurfaceListener mIIJKSurfaceListener;

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    public IJKSurfaceView(Context context) {
        super(context);
        init();
    }

    public IJKSurfaceView(Context context, AttributeSet attrs) {
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
    public void surfaceCreated(SurfaceHolder holder) {
        if (mIIJKSurfaceListener != null) {
            mIIJKSurfaceListener.onSurfaceAvailable(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mIIJKSurfaceListener != null) {
            mIIJKSurfaceListener.onSurfaceSizeChanged(holder.getSurface(), width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //清空释放
        if (mIIJKSurfaceListener != null) {
            mIIJKSurfaceListener.onSurfaceDestroyed(holder.getSurface());
        }
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
    }

    @Override
    public IIJKSurfaceListener getIGSYSurfaceListener() {
        return mIIJKSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IIJKSurfaceListener surfaceListener) {
        getHolder().addCallback(this);
        this.mIIJKSurfaceListener = surfaceListener;
    }

    @Override
    public int getSizeH() {
        return getHeight();
    }

    @Override
    public int getSizeW() {
        return getWidth();
    }

    @Override
    public Bitmap initCover() {
        return null;
    }

    @Override
    public Bitmap initCoverHigh() {
        return null;
    }

    /**
     * 获取截图
     *
     * @param shotHigh 是否需要高清的
     */
    public void taskShotPic(IJKVideoShotListener IJKVideoShotListener, boolean shotHigh) {
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    public void saveFrame(final File file, final boolean high, final IJKVideoShotSaveListener IJKVideoShotSaveListener) {
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
    public static IJKSurfaceView addSurfaceView(Context context, ViewGroup textureViewContainer, int rotate,
                                                final IIJKSurfaceListener gsySurfaceListener,
                                                final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        IJKSurfaceView showSurfaceView = new IJKSurfaceView(context);
        showSurfaceView.setIGSYSurfaceListener(gsySurfaceListener);
        showSurfaceView.setVideoParamsListener(videoParamsListener);
        showSurfaceView.setRotation(rotate);
        IJKRenderView.addToParent(textureViewContainer, showSurfaceView);
        return showSurfaceView;
    }

}
