package com.bdb.lottery.datasource.app

import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.app.data.ApkVersionData
import com.bdb.lottery.datasource.app.data.CustomServiceData
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import com.bdb.lottery.utils.ui.app.Apps
import javax.inject.Inject

class AppRemoteDs @Inject constructor(
    private val retrofitWrapper: RetrofitWrapper,
    private val appApi: AppApi,
) {
    //region 获取平台参数
    fun platformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.observe(
            appApi.platformParams(),
            success
        )
    }

    //预缓存：平台参数
    fun preCachePlatformParams() {
        retrofitWrapper.preCache(URL.URL_PLATFORM_PARAMS, appApi.platformParams())
    }

    //缓存优先：平台参数
    fun cachePrePlatformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.cachePre(
            URL.URL_PLATFORM_PARAMS,
            appApi.platformParams(),
            success
        )
    }
    //endregion

    //预缓存：客服线路
    fun preCacheCustomServiceUrl() {
        retrofitWrapper.preCache(
            URL.URL_CUSTOM_SERVICE,
            appApi.customservice()
        )
    }

    //缓存优先：客服线路
    fun cachePreCustomServiceUrl(success: ((CustomServiceData?) -> Unit)? = null) {
        retrofitWrapper.cachePre(
            URL.URL_CUSTOM_SERVICE,
            appApi.customservice(),
            success
        )
    }
    //endregion

    //region apk版本信息
    //预缓存：apk版本信息
    fun preCacheApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.preCache(
            URL.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode()), success
        )
    }

    //缓存优先：apk版本信息
    fun cachePreApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.cachePre(
            URL.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode()), success
        )
    }

    //缓存先行：apk版本信息
    fun cachePriApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.cachePri(
            URL.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode()), success
        )
    }
    //endregion
}