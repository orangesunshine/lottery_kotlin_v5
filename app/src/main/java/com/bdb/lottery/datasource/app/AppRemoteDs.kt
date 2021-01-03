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

    //预加载
    fun refreshPlatformParamsCache() {
        retrofitWrapper.refreshCache(URL.URL_PLATFORM_PARAMS, appApi.platformParams())
    }

    //缓存优先
    fun cachePrePlatformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.cachePre(
            URL.URL_PLATFORM_PARAMS,
            appApi.platformParams(),
            success
        )
    }
    //endregion

    //region 客服线
    fun preCustomServiceUrl() {
        retrofitWrapper.refreshCache(
            URL.URL_CUSTOM_SERVICE,
            appApi.customservice()
        )
    }

    //优先缓存，网络请求
    fun cachePreCustomServiceUrl(success: ((CustomServiceData?) -> Unit)? = null) {
        retrofitWrapper.cachePre(
            URL.URL_CUSTOM_SERVICE,
            appApi.customservice(),
            success
        )
    }
    //endregion

    //region apk版本信息
    //预加载：刷新本地缓存
    fun refreshApkVersionCache(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.refreshCache(
            URL.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode()), success
        )
    }

    fun cachePreApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.cachePre(
            URL.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode()), success
        )
    }

    fun cachePriApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.cachePri(
            URL.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode()), success
        )
    }
    //endregion
}