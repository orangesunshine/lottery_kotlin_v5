package com.bdb.lottery.app

import android.app.Application
import android.content.Context
import android.os.Process
import androidx.multidex.MultiDex
import com.bdb.lottery.utils.download.OkHttp3Connection
import com.bdb.lottery.utils.download.SSLUtils
import com.bdb.lottery.utils.ui.app.TApp
import com.bdb.lottery.utils.inject.TConfig
import com.bdb.lottery.utils.timber.LogTree
import com.bdb.lottery.utils.ui.activity.TActivityLifecycle
import com.bdb.lottery.widget.CustomHeader
import com.liulishuo.filedownloader.FileDownloader
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class BdbApp : Application() {
    @Inject
    lateinit var tApp: TApp

    @Inject
    lateinit var logTree: LogTree

    @Inject
    lateinit var tConfig: TConfig

    @Inject
    lateinit var tActivityLifecycle: TActivityLifecycle

    companion object {
        lateinit var context: Context
        lateinit var sApp: Application

        //static代码段可以防止内存泄露，设置默认刷新和加载样式
        //设置全局的Header构建器
        init {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
                CustomHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }

            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
                ClassicsFooter(context).setDrawableSize(20F);
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        sApp = this
        context = applicationContext
        registerActivityLifecycleCallbacks(tActivityLifecycle)

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
        RxJavaPlugins.setErrorHandler { }
    }

    private fun initBugly() {
        FileDownloader.setupOnApplicationOnCreate(this).connectionCreator(OkHttp3Connection.Creator(
            SSLUtils.createOkHttp()))
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.setUploadProcess(tApp.isMainProcess())
        // 初始化Bugly
        CrashReport.initCrashReport(
            context,
            tConfig.buglyAppId(),
            tConfig.isDebug(),
            strategy
        )
    }

    private fun initThirdLibs() {
        Timber.plant(logTree)
        AutoSizeConfig.getInstance().setCustomFragment(true)
    }

}