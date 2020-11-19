package com.maiya.call.phone.bean;

import java.io.Serializable;

/**
 * Author : ymc
 * Date   : 2020/5/26  14:35
 * Class  : PhoneMsg
 */
public class PhoneMsg implements Serializable {

    /**
     * mts_key : 1561877
     * prov :
     * city : 上海
     * type : 联通
     */

    private String mts_key;
    private String prov;
    private String city;
    private String type;

    public String getMts_key() {
        return mts_key;
    }

    public void setMts_key(String mts_key) {
        this.mts_key = mts_key;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
