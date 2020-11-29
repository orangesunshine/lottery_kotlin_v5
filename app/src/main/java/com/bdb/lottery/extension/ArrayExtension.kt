package com.bdb.lottery.extension

fun IntArray?.isNullOrEmpty(): Boolean {
    return null == this || this.isEmpty()
}