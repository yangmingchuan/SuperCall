// ITelephony.aidl
package com.android.internal.telephony;

// Declare any non-default types here with import statements

interface ITelephony {
    //挂断电话
    boolean endCall();

    //接听电话
    void answerRingingCall();
}
