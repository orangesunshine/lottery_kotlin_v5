package com.bdb.lottery.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

open class BaseService : Service() {

    override fun onCreate() {
        super.onCreate()
        ServiceManager.push(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceManager.pop(this)
    }
}