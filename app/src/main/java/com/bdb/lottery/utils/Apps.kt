package com.bdb.lottery.utils

import android.app.ActivityManager
import android.content.Context


object Apps {
    fun getProcessName(cxt: Context, pid: Int): String? {
        return (cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .runningAppProcesses.find { it.pid == pid }?.processName ?: null
    }
}