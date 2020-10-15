package com.bdb.lottery.app

import android.app.Application
import android.content.Context
import android.os.Process
import androidx.multidex.MultiDex
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.utils.Devices
import com.bdb.lottery.utils.Screens
import com.bdb.lottery.utils.Sizes
import com.bdb.lottery.utils.timber.LogTree
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class LotteryApp : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        initThirdLibs()

        initUtils()

        initBugly()
    }

    private fun initBugly() {
        val context = applicationContext
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                getProcessName()
            } else {
            }
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.setUploadProcess(processName == null || processName == packageName)
        // 初始化Bugly
        CrashReport.initCrashReport(context, "注册时申请的APPID", BuildConfig.DEBUG, strategy)
    }

    private fun initThirdLibs() {
        MMKV.initialize(this)
        Timber.plant(LogTree())
        Timber.d("i am timber")
    }

    private fun initUtils() {
        Devices.context = applicationContext
        Devices.mmkv = MMKV.defaultMMKV()
        Screens.context = applicationContext
        Sizes.context = applicationContext
    }
}