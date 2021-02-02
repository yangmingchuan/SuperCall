package com.ymc.lib_ijk.ijk.render.view;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;

import com.ymc.lib_ijk.ijk.listener.IJKVideoShotListener;
import com.ymc.lib_ijk.ijk.listener.IJKVideoShotSaveListener;
import com.ymc.lib_ijk.ijk.render.glrender.IJKVideoGLViewBaseRender;
import com.ymc.lib_ijk.ijk.render.view.listener.IIJKSurfaceListener;
import com.ymc.lib_ijk.ijk.utils.MeasureHelper;

import java.io.File;

/**
 * Created by guoshuyu on 2018/1/29.
 */

public interface IIJKRenderView {

    IIJKSurfaceListener getIGSYSurfaceListener();

    /**
     * Surface变化监听，必须
     */
    void setIGSYSurfaceListener(IIJKSurfaceListener surfaceListener);

    /**
     * 当前view高度，必须
     */
    int getSizeH();

    /**
     * 当前view宽度，必须
     */
    int getSizeW();

    /**
     * 实现该接口的view，必须
     */
    View getRenderView();

    /**
     * 渲染view通过MeasureFormVideoParamsListener获取视频的相关参数，必须
     */
    void setVideoParamsListener(MeasureHelper.MeasureFormVideoParamsListener listener);

    /**
     * 截图
     */
    void taskShotPic(IJKVideoShotListener IJKVideoShotListener, boolean shotHigh);

    /**
     * 保存当前帧
     */
    void saveFrame(final File file, final boolean high, final IJKVideoShotSaveListener IJKVideoShotSaveListener);

    /**
     * 获取当前画面的bitmap，没有返回空
     */
    Bitmap initCover();

    /**
     * 获取当前画面的高质量bitmap，没有返回空
     */
    Bitmap initCoverHigh();

    void onRenderResume();

    void onRenderPause();

    void releaseRenderAll();

    void setRenderMode(int mode);

    void setRenderTransform(Matrix transform);

    void setGLRenderer(IJKVideoGLViewBaseRender renderer);

    void setGLMVPMatrix(float[] MVPMatrix);

    void setGLEffectFilter(IJKVideoGLView.ShaderInterface effectFilter);

}
