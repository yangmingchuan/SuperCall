package com.maiya.call.phone.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.maiya.call.App;


/**
 * 通知栏 管理
 */

public class TaskServiceManager {

    private static TaskServiceBinder stepServiceBinder;

    public static void bindStepService(Intent intent) {
        final Context applicationContext = App.Companion.getContext();
        if (intent == null || applicationContext == null) {
            return;
        }
        try {
            if (stepServiceBinder !=null && stepServiceBinder.isBinderAlive()) {
                CallListenerService service = stepServiceBinder.getService();
                if (service !=null) {
                    service.forceForeground(intent);
                }
                return;
            }

            applicationContext.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    if (binder instanceof TaskServiceBinder) {
                        stepServiceBinder = (TaskServiceBinder) binder;
                        CallListenerService service = stepServiceBinder.getService();
                        if (service != null) {
                            service.forceForeground(intent);
                        }
                    }
                    try {
                        applicationContext.unbindService(this);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            }, Context.BIND_AUTO_CREATE);
        } catch (SecurityException e) {
        }
    }
}
