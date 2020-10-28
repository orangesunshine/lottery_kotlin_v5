package com.bdb.lottery.biz.splash

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.app.data.ConfigData
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.datasource.domain.DomainRemoteDs
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.Configs
import com.bdb.lottery.utils.cache.Cache
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ActivityContext
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

class SplashViewModel @ViewModelInject @Inject constructor(
    @ActivityContext private val context: Context,
    private val remoteDomainDs: DomainRemoteDs,
    private val localDomainDs: DomainLocalDs,
    private val appDs: AppRemoteDs
) : ViewModel() {
    val ldDomainRet = LiveDataWraper<Boolean>()

    //初始化域名
    fun initDomain() {
        Timber.d("initDomain")
        if (Configs.isDebug()) {
            Timber.d("isDebug")
            remoteDomainDs.getDebugFrontConfig({
                //线上域名获取成功
                onDomainSuccess(it)
            }, {
                onOnlineDomainError()
            })
        } else {
            /**
             * 1.读取阿里云，七牛云域名撇脂文件，@拆分域名
             */
            remoteDomainDs.getOnlineDomain({
                //线上域名获取成功
                onDomainSuccess(it)
            }, {
                onOnlineDomainError()
            })
        }
    }

    //获取客服
    fun getCustomService() {
        appDs.getCustomServiceUrl {
            it?.let {
                if (it.kefuxian.isSpace())
                    Cache.putString(ICache.CUSTOM_SERVICE_URL_CACHE, it.kefuxian)
            }
        }
    }

    //获取apk版本
    fun getApkVersion() {
        appDs.getAPkVeresion {
            it?.let {
                //缓存apk版本信息
                Cache.putString(ICache.APK_VERSION_CACHE, Gson().toJson(it))
                //发送粘性事件，MainActivity打开处理
                EventBus.getDefault().postSticky(it)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 辅助方法
    ///////////////////////////////////////////////////////////////////////////
    //域名获取成功回调
    fun onDomainSuccess(it: ConfigData?) {
        getCustomService()
        getApkVersion()
        val toUri = it?.WebMobileUrl?.toUri()
        val scheme = toUri?.scheme
        val host = toUri?.host
        val authority = toUri?.authority
        val port = toUri?.port
        val domain =
            scheme + "://" + if (host.isSpace()) host else authority + if (-1 != port) ":${port}" else ""
        //保存并缓存域名
        Timber.d("onDomainSuccess__domain: ${domain}")
        localDomainDs.saveDomain(domain)
        ldDomainRet.setData(true)
    }

    //线上域名获取失败回调
    fun onOnlineDomainError() {
        //本地域名配置
        val localDomainStringId = context.resources.getIdentifier(
            "local_http_url",
            "string",
            BuildConfig.APPLICATION_ID
        )
        if (localDomainStringId > 0) {
            //读取本地域名配置
            remoteDomainDs.getLocalDomain(
                { onDomainSuccess(it) },
                {
                    //本地域名获取失败
                    if (BuildConfig.SHOW_DOALOG_ON_DOMAIN_ERROR) ldDomainRet.setData(
                        false
                    )
                })
        } else {
            //线上域名获取失败，没有配置本地域名，提示
            if (BuildConfig.SHOW_DOALOG_ON_DOMAIN_ERROR) ldDomainRet.setData(false)
        }
    }
}