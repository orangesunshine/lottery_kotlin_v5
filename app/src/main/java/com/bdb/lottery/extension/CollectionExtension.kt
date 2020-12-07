package com.bdb.lottery.extension

fun IntArray?.isNullOrEmpty(): Boolean {
    return null == this || this.isEmpty()
}

fun List<*>?.indexValid(index: Int): Boolean {
    return this?.let { if (it.isNotEmpty()) index in this.indices else false } ?: false
}

fun Array<*>?.indexValid(index: Int): Boolean {
    return this?.let { if (it.isNotEmpty()) index in this.indices else false } ?: false
}