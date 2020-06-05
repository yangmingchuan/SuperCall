package com.maiya.leetcode.phone.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract

/**
 * Author : ymc
 * Date   : 2020/5/25  20:14
 * Class  : ContactUtil
 */

object ContactUtil {
    private var cursor: Cursor? = null

    /**
     * 根据电话号码获取联系人
     */
    @JvmStatic
    @SuppressLint("SimpleDateFormat")
    fun getContentCallLog(mContext: Context?, number: String?): String? {
        try {
            var numberPerson: String?
            val cr = mContext?.contentResolver
            cursor = cr?.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            while (cursor!!.moveToNext()) {
                val nameFieldColumnIndex = cursor?.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                val contact = nameFieldColumnIndex?.let { cursor?.getString(it) }
                //取得电话号码
                val contactId = cursor?.getColumnIndex(ContactsContract.Contacts._ID)?.let { cursor?.getString(it) }
                val phone = cr?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null)
                while (phone!!.moveToNext()) {
                    var phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    phoneNumber = phoneNumber.replace("-", "")
                    phoneNumber = phoneNumber.replace(" ", "")
                    if (number == phoneNumber) {
                        numberPerson = contact
                        return numberPerson
                    }
                    //LogUtils.e("姓名：$contact ，电话：$PhoneNumber ")
                }
            }
            return null
        } catch (e: Exception) {
            return null
        } finally {
            cursor?.close()
        }
    }
}