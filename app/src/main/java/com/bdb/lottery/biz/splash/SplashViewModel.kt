package com.bdb.lottery.biz.splash

import androidx.core.net.toUri
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.base.viewmodel.BaseViewModel
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.LiveDataWraper
import com.bdb.lottery.datasource.appData.ConfigLocalDataSource.getDomain
import com.bdb.lottery.datasource.appData.ConfigLocalDataSource.saveDomain
import com.bdb.lottery.datasource.appData.ConfigRemoteDataSource
import com.bdb.lottery.datasource.appData.data.CustomServiceData
import com.bdb.lottery.extension.nNullEmpty
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.NetAdapter
import javax.inject.Inject

class SplashViewModel @ViewModelInject @Inject constructor(
    private val config: ConfigRemoteDataSource
) : BaseViewModel() {
    val ldDialogOnDomainError = LiveDataWraper<Boolean>()

    //初始化域名
    fun initDomain() {
        config.getDomainNdCallback {
            if (null == it) {
                //获取域名失败
                if (BuildConfig.SHOW_DOALOG_ON_DOMAIN_ERROR) {
                    ldDialogOnDomainError.setData(true)
                }

                getDomain()
            } else {
                val scheme = it?.WebMobileUrl?.toUri()?.scheme
                val host = it?.WebMobileUrl?.toUri()?.host
                val authority = it?.WebMobileUrl?.toUri()?.authority
                val domain = scheme + "://" + if (host.nNullEmpty()) host else authority
                //保存并缓存域名
                saveDomain(domain)
            }
        }
    }

    //获取客服
    fun getCustomService() {
        config.getCustomServiceUrl(object : NetAdapter<CustomServiceData>() {
            override fun onSuccess(data: CustomServiceData?) {
                //缓存客服线
                data?.let {
                    if (it.kefuxian.nNullEmpty())
                        Cache.putString(ICache.CACHE_CUSTOM_SERVICE_URL, it.kefuxian)
                }
            }
        })
    }

    //获取apk版本
    fun getApkVersion() {

    }
}