package com.bdb.lottery.datasource.app

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.HttpConstUrl.Companion.URL_APK_VERSION
import com.bdb.lottery.const.HttpConstUrl.Companion.URL_CUSTOM_SERVICE
import com.bdb.lottery.datasource.app.data.DataApkVersion
import com.bdb.lottery.datasource.app.data.DataConfig
import com.bdb.lottery.datasource.app.data.DataCustomService
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.POST
import retrofit2.http.Url

interface AppApi {
    //前端配置
    @POST
    fun config(@Url url: String): Observable<BaseResponse<DataConfig>>

    //客服
    @POST(URL_CUSTOM_SERVICE)
    fun customService(): Observable<BaseResponse<DataCustomService>>

    //客服
    @POST(URL_APK_VERSION)
    fun apkversion(): Observable<BaseResponse<DataApkVersion>>
}