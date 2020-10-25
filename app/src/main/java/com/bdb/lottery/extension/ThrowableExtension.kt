package com.bdb.lottery.extension

var Throwable?.msg: String?
    get() = this?.let { if (it.message.nNullEmpty()) it.message else it.cause?.message }
    set(_) {}