package com.ymc.lib_ijk.ijk.listener;


import java.io.File;

/**
 * 截屏保存结果
 * Created by guoshuyu on 2017/9/21.
 */

public interface IJKVideoShotSaveListener {
    void result(boolean success, File file);
}
