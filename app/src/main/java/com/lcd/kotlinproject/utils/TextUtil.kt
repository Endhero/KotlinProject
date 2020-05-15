package com.lcd.kotlinproject.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan

/**
 * Created by liuchangda 2019/ 12/ 12
 */
object TextUtil {
    fun getTextWithColor(strText: String, nColor: Int, nStart: Int, nEnd: Int): SpannableString {
        val spannablestring = SpannableString(strText)
        spannablestring.setSpan(ForegroundColorSpan(nColor), nStart, nEnd, 0)

        return spannablestring
    }

    fun getTextWithSizeAndColor(strText: String, nTextSize: Int, bDip: Boolean, nColor: Int, nStart: Int, nEnd: Int): SpannableString {
        val spannablestring = SpannableString(strText)
        spannablestring.setSpan(AbsoluteSizeSpan(nTextSize, bDip), nStart, nEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannablestring.setSpan(ForegroundColorSpan(nColor), nStart, nEnd, 0)

        return spannablestring
    }

    fun getLastName(str: String) = if (str.length <= 2) str else str.substring(str.length - 2, str.length)
}