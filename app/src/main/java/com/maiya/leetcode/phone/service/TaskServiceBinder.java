package com.maiya.leetcode.phone.service;

import android.os.Binder;

import com.maiya.leetcode.phone.manager.CallListenerService;

import java.lang.ref.WeakReference;

/**
 * Service 管理
 */

public class TaskServiceBinder extends Binder {

    private WeakReference<CallListenerService> weakService;

    /**
     * Inject service instance to weak reference.
     */
    public void onBind(CallListenerService service) {
        this.weakService = new WeakReference<>(service);
    }

    public CallListenerService getService() {
        return weakService == null ? null : weakService.get();
    }
}
