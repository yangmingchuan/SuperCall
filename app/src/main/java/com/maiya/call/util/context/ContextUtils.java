package com.maiya.call.util.context;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.maiya.call.util.LogUtils;

/**
 * Author : ymc
 * Date   : 2020/7/7  14:04
 * Class  : ContextUtils
 */
public class ContextUtils {

    public static boolean isDestroyed(Context context) {
        Activity activity = findActivity(context);
        if (activity == null) {
            return true;
        } else {
            boolean isDestroyed = false;
            if (Build.VERSION.SDK_INT >= 17) {
                isDestroyed = activity.isDestroyed();
            }

            if (activity instanceof FragmentActivity) {
                FragmentManager supportFragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
                isDestroyed = supportFragmentManager.isDestroyed();
            }

            LogUtils.i("activity.getClass().getName()>>" + activity.getClass().getName());
            return isDestroyed;
        }
    }

    public static Activity findActivity(Context context) {
        if (context == null) {
            return null;
        } else {
            if (context instanceof Activity) {
                return (Activity)context;
            } else if (context instanceof ContextWrapper) {
                ContextWrapper wrapper = (ContextWrapper)context;
                return findActivity(wrapper.getBaseContext());
            } else {
                return null;
            }
        }
    }

}
