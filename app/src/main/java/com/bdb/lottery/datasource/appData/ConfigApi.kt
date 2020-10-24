package com.bdb.lottery.datasource.appData

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.HttpConstUrl.Companion.URL_APK_VERSION
import com.bdb.lottery.const.HttpConstUrl.Companion.URL_CUSTOM_SERVICE
import com.bdb.lottery.datasource.appData.data.ApkVersion
import com.bdb.lottery.datasource.appData.data.ConfigData
import com.bdb.lottery.datasource.appData.data.CustomServiceData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface ConfigApi {
    //前端配置
    @POST
    fun config(@Url url: String): Observable<BaseResponse<ConfigData>>

    //客服
    @FormUrlEncoded
    @POST(URL_CUSTOM_SERVICE)
    fun customService(): Observable<BaseResponse<CustomServiceData>>

    //客服
    @FormUrlEncoded
    @POST(URL_APK_VERSION)
    fun apkversion(): Observable<BaseResponse<ApkVersion>>
}