package com.bdb.lottery.extension

fun String.equals(str: String): Boolean {
    return (null != this) && this.equals(str)
}