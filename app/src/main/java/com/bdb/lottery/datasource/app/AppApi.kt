package com.bdb.lottery.datasource.app

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.HttpConstUrl.Companion.URL_APK_VERSION
import com.bdb.lottery.const.HttpConstUrl.Companion.URL_CUSTOM_SERVICE
import com.bdb.lottery.datasource.app.data.ApkVersionData
import com.bdb.lottery.datasource.app.data.CustomServiceData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AppApi {
    //客服
    @POST(URL_CUSTOM_SERVICE)
    fun customservice(): Observable<BaseResponse<CustomServiceData>>

    //客服
    @POST(URL_APK_VERSION)
    @FormUrlEncoded
    fun apkversion(
        @Field("packageName") packageName: String,
        @Field("version") versionCode: Int
    ): Observable<BaseResponse<ApkVersionData>>
}