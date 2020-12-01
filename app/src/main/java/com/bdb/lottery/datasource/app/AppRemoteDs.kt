package com.bdb.lottery.datasource.app

import com.bdb.lottery.const.IUrl
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
    fun prePlatformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.preload(IUrl.URL_PLATFORM_PARAMS, appApi.platformParams(), success)
    }

    //缓存优先
    fun cachePriPlatformParams(success: ((PlatformData?) -> Unit)? = null) {
        retrofitWrapper.cachePriLoad(
            IUrl.URL_PLATFORM_PARAMS,
            appApi.platformParams(),
            success
        )
    }
    //endregion

    //region 客服线
    fun preCustomServiceUrl() {
        retrofitWrapper.preload(
            IUrl.URL_CUSTOM_SERVICE,
            appApi.customservice()
        )
    }

    //优先缓存，网络请求
    fun cachePriCustomServiceUrl(success: ((CustomServiceData?) -> Unit)? = null) {
        retrofitWrapper.cachePriLoad(
            IUrl.URL_CUSTOM_SERVICE,
            appApi.customservice(),
            success
        )
    }
    //endregion

    //region apk版本信息
    //预加载
    fun preApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.preload(
            IUrl.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode()), success
        )
    }

    fun cachePriApkVersion(success: ((ApkVersionData?) -> Unit)? = null) {
        retrofitWrapper.cachePriLoad(
            IUrl.URL_APK_VERSION,
            appApi.apkVersion("android", Apps.getAppVersionCode()), success
        )
    }
    //endregion
}