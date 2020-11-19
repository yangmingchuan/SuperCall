package com.maiya.call.phone.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.maiya.call.util.LogUtils

/**
 * Author : ymc
 * Date   : 2020/5/25  20:14
 * Class  : ContactUtil
 */

object ContactUtil {
    /**
     * 根据电话号码获取联系人
     */
    @JvmStatic
    fun getContentCallLog(mContext: Context?, number: String?): ContactInfo? {
        try {
            mContext?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    , arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    , ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                    , ContactsContract.CommonDataKinds.Phone.NUMBER)
                    , null
                    , null
                    , null
            )?.use { phoneCursor ->
                while (phoneCursor.moveToNext()) {
                    val columnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    if (columnIndex < 0) {
                        continue
                    }
                    var phoneNumber = phoneCursor.getString(columnIndex)
                    phoneNumber = phoneNumber?.replace("-", "")?.replace(" ", "")
                    if (number == phoneNumber) {
                        var displayName = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        if (displayName == null) {
                            displayName = phoneNumber
                        }
                        return ContactInfo(displayName
                                , phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)))
                    }
                }
            }
            return null
        } catch (e: Exception) {
            LogUtils.e(e)
            return null
        }
    }

    data class ContactInfo(val displayName: String, val photoUri: String?)
}