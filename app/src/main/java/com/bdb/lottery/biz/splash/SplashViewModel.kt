package com.bdb.lottery.biz.splash

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.base.viewmodel.BaseViewModel
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.LiveDataWraper
import com.bdb.lottery.datasource.appData.ConfigLocalDataSource.saveDomain
import com.bdb.lottery.datasource.appData.ConfigRemoteDataSource
import com.bdb.lottery.datasource.appData.data.DataConfig
import com.bdb.lottery.extension.nNullEmpty
import com.bdb.lottery.utils.cache.Cache
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ActivityContext
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

class SplashViewModel @ViewModelInject @Inject constructor(
    @ActivityContext private val context: Context,
    private val config: ConfigRemoteDataSource
) : BaseViewModel() {
    val ldDomainRet = LiveDataWraper<Boolean>()

    //初始化域名
    fun initDomain() {
        /**
         * 1.读取阿里云，七牛云域名撇脂文件，@拆分域名
         */
        config.getOnlineDomain({
            //线上域名获取成功
            onDomainSuccess(it)
        }, {
            //本地域名配置
            val localDomainStringId = context.resources.getIdentifier(
                "local_http_url",
                "string",
                BuildConfig.APPLICATION_ID
            )
            if (localDomainStringId > 0) {
                //读取本地域名配置
                config.getLocalDomain(
                    { onDomainSuccess(it) },
                    {
                        //提示
                        if (BuildConfig.SHOW_DOALOG_ON_DOMAIN_ERROR) ldDomainRet.setData(
                            false
                        )
                    })
            } else {
                //线上域名获取失败，没有配置本地域名，提示
                if (BuildConfig.SHOW_DOALOG_ON_DOMAIN_ERROR) ldDomainRet.setData(false)
            }

        })
    }

    //获取客服
    fun getCustomService() {
        config.getCustomServiceUrl {
            it?.let {
                if (it.kefuxian.nNullEmpty())
                    Cache.putString(ICache.CACHE_CUSTOM_SERVICE_URL, it.kefuxian)
            }
        }
    }

    //获取apk版本
    fun getApkVersion() {
        config.getAPkVeresion {
            it?.let {
                //缓存apk版本信息
                Cache.putString(ICache.CACHE_APK_VERSION, Gson().toJson(it))
                //发送粘性事件，MainActivity打开处理
                EventBus.getDefault().postSticky(it)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 辅助方法
    ///////////////////////////////////////////////////////////////////////////
    fun onDomainSuccess(it: DataConfig?) {
        getCustomService()
        getApkVersion()
        val scheme = it?.WebMobileUrl?.toUri()?.scheme
        val host = it?.WebMobileUrl?.toUri()?.host
        val authority = it?.WebMobileUrl?.toUri()?.authority
        val domain = scheme + "://" + if (host.nNullEmpty()) host else authority
        //保存并缓存域名
        saveDomain(domain)
        ldDomainRet.setData(true)
    }
}