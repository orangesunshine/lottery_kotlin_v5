package com.bdb.lottery.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TThread @Inject constructor() {

    fun runOnUiThread(runnable: Runnable) {
        AndroidSchedulers.mainThread().scheduleDirect(runnable)
    }

    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
        AndroidSchedulers.mainThread().scheduleDirect(runnable, delayMillis, TimeUnit.MILLISECONDS)
    }

//    private val HANDLER = Handler(Looper.getMainLooper())
//    fun runOnUiThread(runnable: Runnable) {
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            runnable.run()
//        } else {
//            HANDLER.post(runnable)
//        }
//    }
//
//    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
//        HANDLER.postDelayed(runnable, delayMillis)
//    }
}