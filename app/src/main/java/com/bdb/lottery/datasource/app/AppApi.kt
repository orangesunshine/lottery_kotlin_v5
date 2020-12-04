package com.bdb.lottery.datasource.app

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.app.data.ApkVersionData
import com.bdb.lottery.datasource.app.data.CustomServiceData
import com.bdb.lottery.datasource.app.data.PlatformData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AppApi {

    //获取前端配置
    @POST(URL.URL_PLATFORM_PARAMS)
    fun platformParams(): Observable<BaseResponse<PlatformData?>>

    //客服
    @POST(URL.URL_CUSTOM_SERVICE)
    fun customservice(): Observable<BaseResponse<CustomServiceData?>>

    //版本信息
    @POST(URL.URL_APK_VERSION)
    @FormUrlEncoded
    fun apkVersion(
        @Field("packageName") packageName: String,
        @Field("version") versionCode: Int
    ): Observable<BaseResponse<ApkVersionData?>>
}