package com.maiya.call.phone.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.maiya.call.phone.service.CallListenerService;


/**
 * 适配 android 6.0 电话挂断
 * 将app拉到前台
 */

public class ForegroundActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ringTong", "ForegroundActivity  onCreate");
        next(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        next(intent);
    }


    private void next(Intent intent) {
        if (intent==null) {
            delayFinishSelf();
            return;
        }
        intent.setAction(CallListenerService.ACTION_PHONE_CALL);
        intent.setClass(this, CallListenerService.class);
        startService(intent);
        Log.e("ringTong", "ForegroundActivity next");
        delayFinishSelf();
    }

    private void delayFinishSelf() {
        new Handler().postDelayed(() -> finish(), 500);
    }

}
