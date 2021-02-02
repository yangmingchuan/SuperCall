package com.ymc.lib_ijk.ijk.render.view.listener;


import com.ymc.lib_ijk.ijk.render.glrender.IJKVideoGLViewBaseRender;

/**
 * GL渲染错误
 * Created by guoshuyu on 2018/1/14.
 */
public interface IJKVideoGLRenderErrorListener {
    /**
     *
     * @param render
     * @param Error 错误文本
     * @param code 错误代码
     * @param byChangedRenderError 错误是因为切换effect导致的
     */
    void onError(IJKVideoGLViewBaseRender render, String Error, int code, boolean byChangedRenderError);
}
