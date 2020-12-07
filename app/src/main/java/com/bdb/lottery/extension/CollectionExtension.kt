package com.bdb.lottery.extension

fun IntArray?.isNullOrEmpty(): Boolean {
    return null == this || this.isEmpty()
}

fun List<*>?.validIndex(index: Int): Boolean {
    return this?.let { if (it.isNotEmpty()) index in this.indices else false } ?: false
}

fun Array<*>?.validIndex(index: Int): Boolean {
    return this?.let { if (it.isNotEmpty()) index in this.indices else false } ?: false
}