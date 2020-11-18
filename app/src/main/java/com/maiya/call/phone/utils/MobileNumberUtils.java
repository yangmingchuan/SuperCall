package com.maiya.call.phone.utils;

import android.content.Context;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.util.Locale;

/**
 * Author : ymc
 * Date   : 2020/5/25  14:05
 * Class  : MobileNumberUtils
 */
public class MobileNumberUtils {
    private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private static PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

    private static PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
    private static String LANGUAGE ="CN";

    //获取手机号码运营商
    public static String getCarrier(Context context, String phoneNumber, int countryCode) {
        Phonenumber.PhoneNumber referencePhonenumber = new Phonenumber.PhoneNumber();
        try {
            referencePhonenumber = phoneNumberUtil.parse(phoneNumber,LANGUAGE );
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        //返回结果只有英文，自己转成成中文
        String carrierEn = carrierMapper.getNameForNumber(referencePhonenumber, Locale.ENGLISH);
        String carrierZh = "";
        if (countryCode == 86 && Locale.CHINA.getCountry().equals(Locale.getDefault().getCountry())) {
            switch (carrierEn) {
                case "China Mobile":
                    carrierZh += "中国移动";
                    break;
                case "China Unicom":
                    carrierZh += "中国联通";
                    break;
                case "China Telecom":
                    carrierZh += "中国电信";
                    break;
                default:
                    break;
            }
            return carrierZh;
        } else {
            return carrierEn;
        }
    }
    //获取手机号码归属地
    public static String getGeo(String phoneNumber){
        Phonenumber.PhoneNumber referencePhonenumber = null;
        try {
            referencePhonenumber = phoneNumberUtil.parse(phoneNumber, LANGUAGE);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        //手机号码归属城市 referenceRegion
        return geocoder.getDescriptionForNumber(referencePhonenumber, Locale.CHINA);
    }
}
