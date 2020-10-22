package com.bdb.lottery.app

import android.app.Application
import android.content.Context
import android.os.Process
import androidx.multidex.MultiDex
import com.bdb.lottery.utils.*
import com.bdb.lottery.utils.timber.LogTree
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class LotteryApp : Application() {
    companion object {
        lateinit var context: Context

        fun globalContext(): Context {
            return context
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        initThirdLibs()

        initUtils()

        initBugly()
    }

    private fun initBugly() {
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                getProcessName()
            } else {
                Apps.getProcessName(Process.myPid())
            }
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.setUploadProcess(processName == null || processName == packageName)
        // 初始化Bugly
        CrashReport.initCrashReport(
            context,
            Configs.buglyAppId(),
            Configs.isDebug(),
            strategy
        )
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