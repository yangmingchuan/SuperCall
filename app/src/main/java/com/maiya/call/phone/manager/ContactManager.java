package com.maiya.call.phone.manager;

import android.content.Context;
import android.os.Looper;

import com.maiya.call.phone.utils.ContactUtil;
import com.maiya.call.phone.utils.ThreadManager;
import com.ymc.ijkplay.utils.AppHandlerUtil;

public class ContactManager {

    public static void getContentCallLog(Context mContext, String number, Callback callBack) {
        ThreadManager.execute(() -> {
            ContactUtil.ContactInfo contentCallLog = ContactUtil.getContentCallLog(mContext, number);
            callFinish(contentCallLog, callBack);
        });
    }

    public static void callFinish(ContactUtil.ContactInfo log, Callback callBack) {
        if (callBack == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            callBack.onFinish(log);
            return;
        }
        AppHandlerUtil.getMainHandler().post((Runnable) () -> callBack.onFinish(log));
    }

    public interface Callback {
        void onFinish(ContactUtil.ContactInfo contentCallLog);
    }
}
