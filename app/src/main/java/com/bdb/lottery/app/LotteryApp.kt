package com.bdb.lottery.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LotteryApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}