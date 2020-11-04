package com.bdb.lottery.datasource.app

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.app.data.ApkVersionData
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.cache.Cache
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import com.google.gson.GsonBuilder
import javax.inject.Inject

class AppRemoteDs @Inject constructor(
    val retrofitWrapper: RetrofitWrapper,
    private val appApi: AppApi
) {

    //获取平台参数
    fun getPlatformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.observe(
            appApi.platformParams(),
            success
        )
    }

    //优先读缓存
    fun cachePlatformParams(success: ((PlatformData?) -> Unit)? = null) {
        val platform = Cache.getString(ICache.PLATEFORM_CACHE)
        if (!platform.isSpace()) {
            val platformData =
                GsonBuilder().create().fromJson(platform, PlatformData::class.java)
            platformData?.let { success?.invoke(it) }
            return
        }
        getPlatformParams {
            success?.invoke(it)
            it?.let { Cache.getString(ICache.PLATEFORM_CACHE, GsonBuilder().create().toJson(it)) }
        }
    }

    //客服
    fun getCustomServiceUrl(success: ((String?) -> Unit)? = null) {
        retrofitWrapper.observe(appApi.customservice(), {
            it?.let {
                val kefuxian = it.kefuxian
                success?.invoke(kefuxian)
            }
        })
    }

    //优先缓存，网络请求
    fun cacheCustomServiceUrl(success: ((String?) -> Unit)? = null) {
        val customServiceUrl = Cache.getString(ICache.CUSTOM_SERVICE_URL_CACHE)
        if (!customServiceUrl.isSpace()) {
            success?.invoke(customServiceUrl)
            return
        }
        getCustomServiceUrl {
            success?.invoke(it)
            it?.let { Cache.getString(ICache.CUSTOM_SERVICE_URL_CACHE, it) }
        }
    }

    //获取apk版本信息
    fun getAPkVeresion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.observe(
            appApi.apkversion("android", Apps.getAppVersionCode(BdbApp.context)),
            success
        )
    }
}