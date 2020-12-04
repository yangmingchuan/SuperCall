package com.maiya.call.phone.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.maiya.call.App
import kotlin.math.abs

/**
 * @ClassName: [ExitAppHelper]
 * @Description: 双击退出
 *　　　　
 * Created by admin at 2020-06-23
 * @Email xiaosw0802@163.com
 */
object ExitAppHelper {
    private val TAG = "ExitAppHelper"
    private var exitTime = 0L

    @JvmOverloads
    @JvmStatic
    fun checkExit() : Boolean {
        if (abs(System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(App.context,"再按一次退出程序",Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
            return false
        }
        return true
    }

    @JvmStatic
    fun moveTaskToBack(activity: Activity?) : Boolean {
        return activity?.let { act ->
            act.moveTaskToBack(true).also {
                Log.e(TAG, "moveTaskToBack:  $it")
            }
        } ?: false
    }
}