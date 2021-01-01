package com.bdb.lottery.extension

import android.text.TextUtils
import java.util.regex.Pattern

fun CharSequence?.isSpace(): Boolean {
    return this?.let {
        val len = this.length
        if (0 == len) return@let true
        for (i in 0 until len) {
            if (!Character.isWhitespace(this[i])) return@let false
        }
        true
    } ?: true
}

fun CharSequence?.isDomainUrl(): Boolean {
    val regex =
        "((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(:[0-9]{2,5})?(/)?"
    return !this.isSpace() && Pattern.matches(regex, this)
}

fun CharSequence?.equalsNSpace(other: CharSequence?): Boolean {
    return !isSpace() && TextUtils.equals(this, other)
}

