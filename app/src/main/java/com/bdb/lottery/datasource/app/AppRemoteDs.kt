package com.bdb.lottery.datasource.app

import android.content.Context
import com.bdb.lottery.datasource.app.data.ApkVersionData
import com.bdb.lottery.datasource.app.data.CustomServiceData
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Inject

class AppRemoteDs @Inject constructor(
    @ApplicationContext private val context: Context,
    private var retrofit: Retrofit
) {
    val appApi = retrofit.create(AppApi::class.java)

    //客服
    fun getCustomServiceUrl(success: ((CustomServiceData?) -> Any?)? = null) {
        Retrofits.observe(appApi.customservice(), success)
    }

    //获取apk版本信息
    fun getAPkVeresion(success: ((ApkVersionData?) -> Any?)? = null) {
        Retrofits.observe(appApi.apkversion("android", Apps.getAppVersionCode(context)), success)
    }
}