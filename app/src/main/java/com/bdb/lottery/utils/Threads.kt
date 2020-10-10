package com.bdb.lottery.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

object Threads {
    private val UTIL_POOL =
        Executors.newFixedThreadPool(3)
    private val UTIL_HANDLER = Handler(Looper.getMainLooper())

    fun <T> doAsync(task: Task<T>?): Task<T>? {
        UTIL_POOL.execute(task)
        return task
    }

    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            UTIL_HANDLER.post(runnable)
        }
    }

    fun runOnUiThreadDelayed(
        runnable: Runnable?,
        delayMillis: Long
    ) {
        UTIL_HANDLER.postDelayed(runnable, delayMillis)
    }

    abstract class Task<Result>(private val mCallback: Callback<Result>) : Runnable {
        @Volatile
        private var state = NEW
        abstract fun doInBackground(): Result
        override fun run() {
            try {
                val t = doInBackground()
                if (state != NEW) return
                state = COMPLETING
                UTIL_HANDLER.post { mCallback.onCall(t) }
            } catch (th: Throwable) {
                if (state != NEW) return
                state = EXCEPTIONAL
            }
        }

        fun cancel() {
            state = CANCELLED
        }

        val isDone: Boolean
            get() = state != NEW

        val isCanceled: Boolean
            get() = state == CANCELLED

        companion object {
            private const val NEW = 0
            private const val COMPLETING = 1
            private const val CANCELLED = 2
            private const val EXCEPTIONAL = 3
        }

    }

    interface Callback<T> {
        fun onCall(data: T)
    }
}