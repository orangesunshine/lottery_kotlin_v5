package com.bdb.lottery.utils

import android.os.Handler
import android.os.Looper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TThread @Inject constructor(){
    private val HANDLER = Handler(Looper.getMainLooper())

    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            HANDLER.post(runnable)
        }
    }

    fun runOnUiThreadDelayed(runnable: Runnable?, delayMillis: Long) {
        HANDLER.postDelayed(runnable, delayMillis)
    }
}