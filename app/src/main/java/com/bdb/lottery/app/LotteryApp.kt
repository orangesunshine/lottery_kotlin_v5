package com.bdb.lottery.app

import android.app.Application
import com.bdb.lottery.utils.Devices
import com.bdb.lottery.utils.Screens
import com.bdb.lottery.utils.Sizes
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class LotteryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initThirdLibs()

        initUtils()
    }

    private fun initThirdLibs() {
        MMKV.initialize(this)
    }

    private fun initUtils() {
        Devices.context = applicationContext
        Devices.mmkv = MMKV.defaultMMKV()
        Screens.context = applicationContext
        Sizes.context = applicationContext
    }
}