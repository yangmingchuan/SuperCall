package com.maiya.call.phone.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maiya.call.R

/**
 * @ClassName: [CallerKeyboardAdapter]
 * @Description:
 *
 * Created by admin at 2020-07-06
 * @Email xiaosw0802@163.com
 */
class CallerKeyboardAdapter
    : BaseQuickAdapter<CallerKeyboardAdapter.CallerKeyboard, CallerKeyboardAdapter.KeyboardHolder>(
        R.layout.item_caller_keyboard, mutableListOf<CallerKeyboard>().also {
    it.add(CallerKeyboard('1', ""))
    it.add(CallerKeyboard('2', "ABC"))
    it.add(CallerKeyboard('3', "DEF"))
    it.add(CallerKeyboard('4', "GHI"))
    it.add(CallerKeyboard('5', "JKL"))
    it.add(CallerKeyboard('6', "MNO"))
    it.add(CallerKeyboard('7', "PQRS"))
    it.add(CallerKeyboard('8', "TUV"))
    it.add(CallerKeyboard('9', "WXYZ"))
    it.add(CallerKeyboard('*', ""))
    it.add(CallerKeyboard('0', "+"))
    it.add(CallerKeyboard('#', ""))
}
) {

    override fun convert(helper: KeyboardHolder, item: CallerKeyboard) {
        helper.tvDigit.text = item.digit.toString()
        helper.tvLetter.text = item.desc
    }

    inner class KeyboardHolder(view: View) : BaseViewHolder(view) {
        val tvDigit: TextView = view.findViewById(R.id.tv_digit)
        val tvLetter: TextView = view.findViewById(R.id.tv_letter)
    }

    class CallerKeyboard(val digit: Char, val desc: String)

}