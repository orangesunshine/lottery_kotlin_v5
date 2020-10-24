package com.bdb.lottery.extension

import android.text.TextUtils
import java.util.regex.Pattern

fun String?.nNullEmpty(): Boolean {
    return this != null && this.length > 0
}

fun String?.nEmptyEquals(str: String?): Boolean {
    return nNullEmpty() && TextUtils.equals(this, str)
}

fun String?.isDomainUrl(): Boolean {
    val regex =
        "((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)?"
    return this.nNullEmpty() && Pattern.matches(regex, this)
}