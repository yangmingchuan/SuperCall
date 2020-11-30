package com.maiya.call.phone

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.maiya.call.R
import com.maiya.call.util.LogUtils


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
        getConName()
        getContentCallLog()
    }

    private val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)

    //根据手机号码查询联系人姓名
    private fun getConName() {
        var displayName: String
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", arrayOf("1-588-677-3361"), null)
        LogUtils.e("cursor displayName count:" + cursor!!.count)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                if (!TextUtils.isEmpty(displayName)) {
                    LogUtils.e("获取的通讯录 姓名是 : $displayName")
                    break
                }
            }
        }
    }


    //获取通话记录
    @SuppressLint("SimpleDateFormat")
    private fun getContentCallLog() {
        //得到ContentResolver对象
        val cr = contentResolver
        //取得电话本中开始一项的光标
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        //向下移动光标
        while (cursor!!.moveToNext()) { //取得联系人名字
            val nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
            val contact = cursor.getString(nameFieldColumnIndex)
            //取得电话号码
            val ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null)
            while (phone!!.moveToNext()) {
                var PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                //格式化手机号
                PhoneNumber = PhoneNumber.replace("-", "")
                PhoneNumber = PhoneNumber.replace(" ", "")
                LogUtils.e("姓名：$contact ，电话：$PhoneNumber ")
            }
        }
        cursor.close()
    }


}
