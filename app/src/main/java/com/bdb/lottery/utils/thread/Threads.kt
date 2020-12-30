package com.bdb.lottery.utils.thread

import android.os.Handler
import android.os.Looper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object Threads {
    fun retrofitUIThread(runnable: Runnable) {
        AndroidSchedulers.mainThread().scheduleDirect(runnable)
    }

    fun retrofitNewThread(runnable: Runnable){
        Schedulers.newThread().scheduleDirect(runnable)
    }

    fun retrofitUIThreadDelayed(runnable: Runnable, delayMillis: Long) {
        AndroidSchedulers.mainThread().scheduleDirect(runnable, delayMillis, TimeUnit.MILLISECONDS)
    }

    fun retrofitNewThreadDelayed(runnable: Runnable, delayMillis: Long) {
        Schedulers.newThread().scheduleDirect(runnable, delayMillis, TimeUnit.MILLISECONDS)
    }

    private val HANDLER = Handler(Looper.getMainLooper())
    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            HANDLER.post(runnable)
        }
    }

    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
        HANDLER.postDelayed(runnable, delayMillis)
    }
}