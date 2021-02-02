package com.ymc.lib_ijk.ijk.render.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.ymc.lib_ijk.ijk.listener.IJKVideoShotListener;
import com.ymc.lib_ijk.ijk.listener.IJKVideoShotSaveListener;
import com.ymc.lib_ijk.ijk.render.IJKRenderView;
import com.ymc.lib_ijk.ijk.render.effect.NoEffect;
import com.ymc.lib_ijk.ijk.render.glrender.IJKVideoGLViewBaseRender;
import com.ymc.lib_ijk.ijk.render.glrender.IJKVideoGLViewSimpleRender;
import com.ymc.lib_ijk.ijk.render.view.listener.GLSurfaceListener;
import com.ymc.lib_ijk.ijk.render.view.listener.IIJKSurfaceListener;
import com.ymc.lib_ijk.ijk.render.view.listener.IJKVideoGLRenderErrorListener;
import com.ymc.lib_ijk.ijk.utils.FileUtils;
import com.ymc.lib_ijk.ijk.utils.MeasureHelper;

import java.io.File;


/**
 * 在videffects的基础上调整的
 * <p>
 * 原 @author sheraz.khilji
 */
@SuppressLint("ViewConstructor")
public class IJKVideoGLView extends GLSurfaceView implements GLSurfaceListener, IIJKRenderView, MeasureHelper.MeasureFormVideoParamsListener {

    private static final String TAG = IJKVideoGLView.class.getName();
    /**
     * 利用布局计算大小
     */
    public static final int MODE_LAYOUT_SIZE = 0;
    /**
     * 利用Render计算大小
     */
    public static final int MODE_RENDER_SIZE = 1;

    private IJKVideoGLViewBaseRender mRenderer;

    private Context mContext;

    private ShaderInterface mEffect = new NoEffect();

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    private GLSurfaceListener mOnGSYSurfaceListener;

    private IIJKSurfaceListener mIIJKSurfaceListener;

    private float[] mMVPMatrix;

    private int mMode = MODE_LAYOUT_SIZE;

    public interface ShaderInterface {
        String getShader(GLSurfaceView mGlSurfaceView);
    }

    public IJKVideoGLView(Context context) {
        super(context);
        init(context);
    }

    public IJKVideoGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setEGLContextClientVersion(2);
        mRenderer = new IJKVideoGLViewSimpleRender();
        measureHelper = new MeasureHelper(this, this);
        mRenderer.setSurfaceView(IJKVideoGLView.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRenderer != null) {
            mRenderer.initRenderSize();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMode == MODE_RENDER_SIZE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
            initRenderMeasure();
        } else {
            measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
            setMeasuredDimension(measureHelper.getMeasuredWidth(), measureHelper.getMeasuredHeight());
        }
    }

    @Override
    public IIJKSurfaceListener getIGSYSurfaceListener() {
        return mIIJKSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IIJKSurfaceListener surfaceListener) {
        setOnGSYSurfaceListener(this);
        mIIJKSurfaceListener = surfaceListener;
    }

    @Override
    public void onSurfaceAvailable(Surface surface) {
        if (mIIJKSurfaceListener != null) {
            mIIJKSurfaceListener.onSurfaceAvailable(surface);
        }
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
    @Override
    public void taskShotPic(IJKVideoShotListener IJKVideoShotListener, boolean shotHigh) {
        if (IJKVideoShotListener != null) {
            setGSYVideoShotListener(IJKVideoShotListener, shotHigh);
            takeShotPic();

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
        setGSYVideoShotListener(IJKVideoShotListener, high);
        takeShotPic();
    }

    @Override
    public View getRenderView() {
        return this;
    }


    @Override
    public void onRenderResume() {
        requestLayout();
        onResume();
    }

    @Override
    public void onRenderPause() {
        requestLayout();
        onPause();

    }

    @Override
    public void releaseRenderAll() {
        requestLayout();
        releaseAll();

    }

    @Override
    public void setRenderMode(int mode) {
        setMode(mode);
    }


    @Override
    public void setRenderTransform(Matrix transform) {
    }

    @Override
    public void setGLRenderer(IJKVideoGLViewBaseRender renderer) {
        setCustomRenderer(renderer);
    }

    @Override
    public void setGLMVPMatrix(float[] MVPMatrix) {
        setMVPMatrix(MVPMatrix);
    }

    /**
     * 设置滤镜效果
     */
    @Override
    public void setGLEffectFilter(IJKVideoGLView.ShaderInterface effectFilter) {
        setEffect(effectFilter);
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

    protected void initRenderMeasure() {
        if (mVideoParamsListener != null && mMode == MODE_RENDER_SIZE) {
            try {
                int videoWidth = mVideoParamsListener.getCurrentVideoWidth();
                int videoHeight = mVideoParamsListener.getCurrentVideoHeight();
                if (this.mRenderer != null) {
                    this.mRenderer.setCurrentViewWidth(measureHelper.getMeasuredWidth());
                    this.mRenderer.setCurrentViewHeight(measureHelper.getMeasuredHeight());
                    this.mRenderer.setCurrentVideoWidth(videoWidth);
                    this.mRenderer.setCurrentVideoHeight(videoHeight);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void initRender() {
        setRenderer(mRenderer);
    }


    public void setGSYVideoGLRenderErrorListener(IJKVideoGLRenderErrorListener videoGLRenderErrorListener) {
        this.mRenderer.setGSYVideoGLRenderErrorListener(videoGLRenderErrorListener);
    }

    /**
     * 设置自定义的render，其他自定义设置会被取消，需要重新设置
     * 在initRender() 前设置才会生效
     *
     * @param CustomRender
     */
    public void setCustomRenderer(IJKVideoGLViewBaseRender CustomRender) {
        this.mRenderer = CustomRender;
        mRenderer.setSurfaceView(IJKVideoGLView.this);
        initRenderMeasure();
    }

    public void setOnGSYSurfaceListener(GLSurfaceListener mGSYSurfaceListener) {
        this.mOnGSYSurfaceListener = mGSYSurfaceListener;
        mRenderer.setGSYSurfaceListener(this.mOnGSYSurfaceListener);
    }

    public void setEffect(ShaderInterface shaderEffect) {
        if (shaderEffect != null) {
            mEffect = shaderEffect;
            mRenderer.setEffect(mEffect);
        }
    }

    public void setMVPMatrix(float[] MVPMatrix) {
        if (MVPMatrix != null) {
            mMVPMatrix = MVPMatrix;
            mRenderer.setMVPMatrix(MVPMatrix);
        }
    }

    public void takeShotPic() {
        mRenderer.takeShotPic();
    }


    public void setGSYVideoShotListener(IJKVideoShotListener listener, boolean high) {
        this.mRenderer.setGSYVideoShotListener(listener, high);
    }

    public int getMode() {
        return mMode;
    }

    /**
     * @param mode MODE_LAYOUT_SIZE = 0,  MODE_RENDER_SIZE = 1
     */
    public void setMode(int mode) {
        this.mMode = mode;
    }

    public void releaseAll() {
        if (mRenderer != null) {
            mRenderer.releaseAll();
        }
    }

    public IJKVideoGLViewBaseRender getRenderer() {
        return mRenderer;
    }

    public ShaderInterface getEffect() {
        return mEffect;
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }

    /**
     * 添加播放的view
     */
    public static IJKVideoGLView addGLView(final Context context, final ViewGroup textureViewContainer, final int rotate,
                                           final IIJKSurfaceListener gsySurfaceListener,
                                           final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener,
                                           final IJKVideoGLView.ShaderInterface effect, final float[] transform,
                                           final IJKVideoGLViewBaseRender customRender, final int renderMode) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        final IJKVideoGLView IJKVideoGLView = new IJKVideoGLView(context);
        if (customRender != null) {
            IJKVideoGLView.setCustomRenderer(customRender);
        }
        IJKVideoGLView.setEffect(effect);
        IJKVideoGLView.setVideoParamsListener(videoParamsListener);
        IJKVideoGLView.setRenderMode(renderMode);
        IJKVideoGLView.setIGSYSurfaceListener(gsySurfaceListener);
        IJKVideoGLView.setRotation(rotate);
        IJKVideoGLView.initRender();
        IJKVideoGLView.setGSYVideoGLRenderErrorListener(new IJKVideoGLRenderErrorListener() {
            @Override
            public void onError(IJKVideoGLViewBaseRender render, String Error, int code, boolean byChangedRenderError) {
                if (byChangedRenderError)
                    addGLView(context,
                            textureViewContainer,
                            rotate,
                            gsySurfaceListener,
                            videoParamsListener,
                            render.getEffect(),
                            render.getMVPMatrix(),
                            render, renderMode);

            }
        });
        if (transform != null && transform.length == 16) {
            IJKVideoGLView.setMVPMatrix(transform);
        }
        IJKRenderView.addToParent(textureViewContainer, IJKVideoGLView);
        return IJKVideoGLView;
    }


}
