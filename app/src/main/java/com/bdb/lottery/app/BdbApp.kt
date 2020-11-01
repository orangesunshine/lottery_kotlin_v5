package com.bdb.lottery.app

import android.app.Application
import android.content.Context
import android.os.Process
import android.os.SystemClock
import android.util.Log
import androidx.multidex.MultiDex
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.Configs
import com.bdb.lottery.utils.timber.LogTree
import com.bdb.lottery.widget.CustomHeader
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber


@HiltAndroidApp
class BdbApp : Application() {
    companion object {
        lateinit var context: Context
        //static代码段可以防止内存泄露，设置默认刷新和加载样式
        //设置全局的Header构建器
        init {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator(object :DefaultRefreshHeaderCreator{
                override fun createRefreshHeader(
                    context: Context,
                    layout: RefreshLayout
                ): RefreshHeader {
                    return CustomHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
                }
            })

            SmartRefreshLayout.setDefaultRefreshFooterCreator(object : DefaultRefreshFooterCreator {
                override fun createRefreshFooter(
                    context: Context,
                    layout: RefreshLayout
                ): RefreshFooter {
                    return ClassicsFooter(context).setDrawableSize(20F);
                }
            })
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        //必须主线程
        MMKV.initialize(this)

        //可以子线程启动
        Thread {
            //设置线程的优先级，不与主线程抢资源
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            Thread.sleep(50)

            initThirdLibs()
            initAutosize()
            initBugly()
            initRxjava()
        }.start()
    }

    private fun initAutosize() {
        AutoSizeConfig.getInstance().setExcludeFontScale(true)
    }

    private fun initRxjava() {
        RxJavaPlugins.setErrorHandler { e: Throwable? -> }
    }

    private fun initBugly() {
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.setUploadProcess(Apps.isMainProcess())
        // 初始化Bugly
        CrashReport.initCrashReport(
            context,
            Configs.buglyAppId(),
            Configs.isDebug(),
            strategy
        )
    }

    private fun initThirdLibs() {
        Timber.plant(LogTree())
        AutoSizeConfig.getInstance().setCustomFragment(true)
    }

}