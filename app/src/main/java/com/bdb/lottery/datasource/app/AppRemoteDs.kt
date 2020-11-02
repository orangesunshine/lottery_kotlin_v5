package com.bdb.lottery.datasource.app

import android.content.Context
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.BaseRemoteDs
import com.bdb.lottery.datasource.app.data.ApkVersionData
import com.bdb.lottery.datasource.app.data.ConfigData
import com.bdb.lottery.datasource.app.data.CustomServiceData
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.cache.Cache
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppRemoteDs @Inject constructor(
    @ApplicationContext private val context: Context,
    private val domainLocalDs: DomainLocalDs,
    private val appApi: AppApi
) : BaseRemoteDs() {

    //获取平台参数
    fun getPlateformParams(success: ((ConfigData?) -> Unit)? = null) {
        retrofitWrapper.observe(
            appApi.plateformParams(domainLocalDs.getDomain() + HttpConstUrl.URL_CONFIG_FRONT),
            success
        )
    }

    //客服
    fun getCustomServiceUrl(success: ((CustomServiceData?) -> Unit)? = null) {
        retrofitWrapper.observe(appApi.customservice(), {
            it?.let {
                success?.invoke(it)
                if (!it.kefuxian.isSpace())
                    Cache.putString(ICache.CUSTOM_SERVICE_URL_CACHE, it.kefuxian)
            }

        })
    }

    //优先缓存，网络请求
    fun cacheCustomServiceUrl(success: ((CustomServiceData?) -> Unit)? = null) {
        retrofitWrapper.observe(appApi.customservice(), success)
    }

    //获取apk版本信息
    fun getAPkVeresion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.observe(
            appApi.apkversion("android", Apps.getAppVersionCode(context)),
            success
        )
    }
}