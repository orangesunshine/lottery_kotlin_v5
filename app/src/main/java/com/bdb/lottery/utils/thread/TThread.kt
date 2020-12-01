package com.bdb.lottery.utils.thread

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TThread @Inject constructor() {

    fun runOnUiThread(runnable: Runnable) {
        Threads.retrofitUIThread(runnable)
    }

    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
        Threads.retrofitUIThreadDelayed(runnable, delayMillis)
    }
}