package com.bdb.lottery.extension

import android.net.Uri
import android.text.TextUtils
import java.util.regex.Pattern

fun String?.nNullEmpty(): Boolean {
    return this != null && this.length > 0
}

fun String?.notNullEquals(str: String?): Boolean {
    return (null != this) && TextUtils.equals(this, str)
}

fun String?.notEmptyEquals(str: String?): Boolean {
    return nNullEmpty() && TextUtils.equals(this, str)
}

fun String?.isNetUrl(): Boolean {
    return notEmptyEquals(this) && Pattern.matches("http(s)?", Uri.parse(this).scheme)
}