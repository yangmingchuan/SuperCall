package com.maiya.leetcode.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import androidx.annotation.StyleRes
import com.maiya.leetcode.R
import com.maiya.leetcode.util.context.ContextUtils

abstract class DelegateDialog @JvmOverloads constructor (
        context: Context,
        @StyleRes themeResId: Int = R.style.PromptDialog
) : Dialog(context, themeResId) {

    private var delegate: DialogDelegate? = null

    inline fun setDefWindowParams() {
        window?.apply {
            val lp = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            attributes = lp
        }
    }

    override fun show() {
        if (ContextUtils.isDestroyed(context)) {
            return
        }
        super.show()
        delegate?.addDialog(this)
    }

    override fun dismiss() {
        if (!isShowing) {
            return
        }
        super.dismiss()
        delegate?.removeDialog(this)
    }

    fun <R : DelegateDialog> setDialogDelegate(delegate: DialogDelegate?) : R {
        this.delegate = delegate
        return this as R
    }
}