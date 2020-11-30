package com.maiya.call.phone.utils

import android.content.Context
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder
import java.util.*

/**
 * Author : ymc
 * Date   : 2020/5/25  14:05
 * Class  : MobileNumberUtils
 */
object MobileNumberUtils {
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()
    private val carrierMapper = PhoneNumberToCarrierMapper.getInstance()
    private val geocoder = PhoneNumberOfflineGeocoder.getInstance()
    private const val LANGUAGE = "CN"

    //获取手机号码运营商
    fun getCarrier(context: Context?, phoneNumber: String?, countryCode: Int): String {
        var referencePhonenumber: PhoneNumber? = PhoneNumber()
        try {
            referencePhonenumber = phoneNumberUtil.parse(phoneNumber, LANGUAGE)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }
        //返回结果只有英文，自己转成成中文
        val carrierEn = carrierMapper.getNameForNumber(referencePhonenumber, Locale.ENGLISH)
        var carrierZh = ""
        return if (countryCode == 86 && Locale.CHINA.country == Locale.getDefault().country) {
            when (carrierEn) {
                "China Mobile" -> carrierZh += "中国移动"
                "China Unicom" -> carrierZh += "中国联通"
                "China Telecom" -> carrierZh += "中国电信"
                else -> {
                }
            }
            carrierZh
        } else {
            carrierEn
        }
    }

    //获取手机号码归属地
    fun getGeo(phoneNumber: String?): String {
        var referencePhonenumber: PhoneNumber? = null
        try {
            referencePhonenumber = phoneNumberUtil.parse(phoneNumber, LANGUAGE)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }
        //手机号码归属城市 referenceRegion
        return geocoder.getDescriptionForNumber(referencePhonenumber, Locale.CHINA)
    }
}