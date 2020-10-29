package com.bdb.lottery.datasource.app

import android.content.Context
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.datasource.app.data.ApkVersionData
import com.bdb.lottery.datasource.app.data.ConfigData
import com.bdb.lottery.datasource.app.data.CustomServiceData
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.net.retrofit.RetrofitModule
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Inject

class AppRemoteDs @Inject constructor(
    @ApplicationContext private val context: Context,
    private val domainLocalDs: DomainLocalDs,
    private val appApi: AppApi
) {

    //获取平台参数
    fun getPlateformParams(success: ((ConfigData?) -> Any?)? = null) {
        Retrofits.observe(
            appApi.plateformParams(domainLocalDs.getDomain() + HttpConstUrl.URL_CONFIG_FRONT),
            success
        )
    }

    //客服
    fun getCustomServiceUrl(success: ((CustomServiceData?) -> Any?)? = null) {
        Retrofits.observe(appApi.customservice(), success)
    }

    //获取apk版本信息
    fun getAPkVeresion(success: ((ApkVersionData?) -> Any?)? = null) {
        Retrofits.observe(appApi.apkversion("android", Apps.getAppVersionCode(context)), success)
    }
}