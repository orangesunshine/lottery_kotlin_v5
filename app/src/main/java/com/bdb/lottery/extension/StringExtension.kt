package com.bdb.lottery.extension

import android.text.TextUtils

fun String?.notNullEquals(str: String?): Boolean {
    return (null != this) && TextUtils.equals(this, str)
}

fun String?.notEmptyEquals(str: String?): Boolean {
    return !TextUtils.isEmpty(this) && TextUtils.equals(this, str)
}