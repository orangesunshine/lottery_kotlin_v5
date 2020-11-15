package com.bdb.lottery.datasource.account

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.IUrl
import com.bdb.lottery.datasource.account.data.BalanceData
import com.bdb.lottery.datasource.account.data.TokenData
import com.bdb.lottery.datasource.account.data.UserInfoData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AccountApi {
    //登录
    @POST(IUrl.URL_LOGIN)
    @FormUrlEncoded
    fun login(
        @Field("jsonString") params: String
    ): Observable<BaseResponse<String?>>


    //试玩
    @POST(IUrl.ULR_LOGIN_TRIAL)
    fun trialPlay(
    ): Observable<BaseResponse<TokenData?>>


    //是否需要验证码
    @GET(IUrl.URL_NEED_VALIDATE_CODE)
    fun needvalidate(
    ): Observable<BaseResponse<Boolean?>>

    //余额
    @POST(IUrl.ULR_BALANCE)
    fun balance(
    ): Observable<BaseResponse<BalanceData?>>

    //用户信息
    @POST(IUrl.URL_USERINFO)
    fun userinfo(
    ): Observable<BaseResponse<UserInfoData?>>
}