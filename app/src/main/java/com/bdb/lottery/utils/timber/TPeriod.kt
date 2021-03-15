package com.bdb.lottery.utils.timber

import android.os.SystemClock
import com.bdb.lottery.extension.isSpace
import timber.log.Timber

object TPeriod {
    private var time: Long = 0
    private var count: Int = 0

    fun print(tag: String? = null) {
        count++
        val curTime = SystemClock.elapsedRealtime()
        if (time > 0) Timber.e("tag: ${tag}-------duration: " + (curTime - time) + ", count: " + count)
        time = curTime
    }

}