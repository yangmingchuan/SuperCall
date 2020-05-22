package com.maiya.leetcode.phone

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.maiya.leetcode.R
import java.text.SimpleDateFormat
import java.util.*


class PhoneListActivity : AppCompatActivity() {
    private val TAG = "ymc-"
    private val callUri: Uri = CallLog.Calls.CONTENT_URI
    private val columns = arrayOf(CallLog.Calls.CACHED_NAME // 通话记录的联系人
            , CallLog.Calls.NUMBER // 通话记录的电话号码
            , CallLog.Calls.DATE // 通话记录的日期
            , CallLog.Calls.DURATION // 通话时长
            , CallLog.Calls.TYPE) // 通话类型}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_list)
    }


    //获取通话记录
    @SuppressLint("SimpleDateFormat")
    private fun getContentCallLog() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val cursor: Cursor? = contentResolver.query(callUri,  // 查询通话记录的URI
                columns
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER // 按照时间逆序排列，最近打的最先显示
        )
        cursor?.let {
            Log.i(TAG, "cursor count:" + it.getCount())
            while (it.moveToNext()) {
                val name: String = it.getString(it.getColumnIndex(CallLog.Calls.CACHED_NAME)) //姓名
                val number: String = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER)) //号码
                val dateLong: Long = it.getLong(it.getColumnIndex(CallLog.Calls.DATE)) //获取通话日期
                val date: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(dateLong))
                val time: String = SimpleDateFormat("HH:mm").format(Date(dateLong))
                val duration: Int = it.getInt(it.getColumnIndex(CallLog.Calls.DURATION)) //获取通话时长，值为多少秒
                val type: Int = it.getInt(it.getColumnIndex(CallLog.Calls.TYPE)) //获取通话类型：1.呼入2.呼出3.未接
                val dayCurrent: String = SimpleDateFormat("dd").format(Date())
                val dayRecord: String = SimpleDateFormat("dd").format(Date(dateLong))
                Log.i(TAG, "Call log: " + "\n"
                        + "name: " + name + "\n"
                        + "phone number: " + number + "\n"
                        + "date: " + date + "\n"
                        + "time: " + time + "\n"
                        + "duration: " + duration + "\n"
                        + "type: " + type + "\n"
                        + "dayCurrent: " + dayCurrent + "\n"
                        + "dayRecord: " + dayCurrent + "\n"
                )
            }

        }

    }


}
