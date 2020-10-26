package com.bdb.lottery.datasource.app

import android.content.Context
import com.bdb.lottery.datasource.app.data.DataApkVersion
import com.bdb.lottery.datasource.app.data.DataCustomService
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named


class AppRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("Url") private var retrofit: Retrofit
) {
    val appApi = retrofit.create(AppApi::class.java)


    //客服
    fun getCustomServiceUrl(success: ((DataCustomService?) -> Any?)? = null) {
        Retrofits.observe(appApi.customService(), success)
    }

    //获取apk版本信息
    fun getAPkVeresion(success: ((DataApkVersion?) -> Any?)? = null) {
        Retrofits.observe(appApi.apkversion(), success)
    }

}