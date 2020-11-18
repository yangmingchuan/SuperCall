package com.maiya.call.dialog

import android.app.Dialog

interface DialogDelegate {

    fun addDialog(dialog: Dialog?)

    fun removeDialog(dialog: Dialog?)

    fun clearDialog()

}