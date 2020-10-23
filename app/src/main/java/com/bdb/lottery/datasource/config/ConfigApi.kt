package com.bdb.lottery.datasource.config

import com.bdb.lottery.base.response.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.POST
import retrofit2.http.Url

interface ConfigApi {
    @POST
    fun frontConfig(@Url url: String): Observable<BaseResponse<FrontConfigData>>
}