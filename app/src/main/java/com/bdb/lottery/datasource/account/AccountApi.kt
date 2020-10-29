package com.bdb.lottery.datasource.account

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.datasource.account.data.TokenData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AccountApi {
    //登录
    @POST(HttpConstUrl.URL_LOGIN)
    @FormUrlEncoded
    fun login(
        @Field("jsonString") params: String
    ): Observable<BaseResponse<String>>


    //试玩
    @POST(HttpConstUrl.URL_LOGIN)
    fun trialPlay(
    ): Observable<BaseResponse<TokenData>>


    //是否需要验证码
    @GET(HttpConstUrl.URL_NEED_VALIDATE_CODE)
    fun needvalidate(
    ): Observable<BaseResponse<Boolean>>
}