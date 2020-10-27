package com.bdb.lottery.utils.timber

import android.util.Log
import com.bdb.lottery.BuildConfig
import timber.log.Timber

class LogTree : Timber.DebugTree() {
    //release可以打印的tag，需要设置local.prop
    val tags = listOf("LOTTERY")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (BuildConfig.DEBUG || (tags.contains(tag) && Log.isLoggable(tag, Log.DEBUG))) {
            super.log(priority, "younger__", message, t)
        }
    }
}