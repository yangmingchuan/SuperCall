package com.ymc.lib_ijk.ijk.render.effect;

import android.opengl.GLSurfaceView;

import com.ymc.lib_ijk.ijk.render.view.IJKVideoGLView;


/**
 * Displays the normal video without any effect.
 *
 * @author sheraz.khilji
 */
public class NoEffect implements IJKVideoGLView.ShaderInterface {
    /**
     * Initialize
     */
    public NoEffect() {
    }

    @Override
    public String getShader(GLSurfaceView mGlSurfaceView) {

        String shader = "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "varying vec2 vTextureCoord;\n"
                + "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
                + "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
                + "}\n";

        return shader;

    }
}