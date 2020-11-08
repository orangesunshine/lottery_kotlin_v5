package com.bdb.lottery.datasource.app

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.datasource.app.data.ApkVersionData
import com.bdb.lottery.datasource.app.data.CustomServiceData
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import javax.inject.Inject

class AppRemoteDs @Inject constructor(
    val retrofitWrapper: RetrofitWrapper,
    private val appApi: AppApi,
) {
    //region 获取平台参数
    fun platformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.observe(
            appApi.platformParams(),
            success
        )
    }

    //预加载
    fun preloadPlatformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.preload(HttpConstUrl.URL_PLATFORM_PARAMS, appApi.platformParams(), success)
    }

    //缓存优先
    fun cacheBeforePlatformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.inlineCacheBeforeLoad(HttpConstUrl.URL_PLATFORM_PARAMS,
            appApi.platformParams(),
            success)
    }
    //endregion

    //region 客服线
    fun preloadCustomServiceUrl() {
        retrofitWrapper.preload(
            HttpConstUrl.URL_CUSTOM_SERVICE,
            appApi.customservice()
        )
    }

    //优先缓存，网络请求
    fun cacheBeforeCustomServiceUrl(success: ((CustomServiceData?) -> Unit)? = null) {
        retrofitWrapper.inlineCacheBeforeLoad(
            HttpConstUrl.URL_CUSTOM_SERVICE,
            appApi.customservice(),
            success
        )
    }
    //endregion

    //region apk版本信息
    //预加载
    fun preloadApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.preload(
            HttpConstUrl.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode(BdbApp.context)), success
        )
    }

    fun cacheBeforeApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.inlineCacheBeforeLoad(
            HttpConstUrl.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode(BdbApp.context)), success
        )
    }
    //endregion
}