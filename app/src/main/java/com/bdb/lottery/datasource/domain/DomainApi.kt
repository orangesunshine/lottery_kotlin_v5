package com.bdb.lottery.datasource.domain

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.datasource.app.data.ConfigData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface DomainApi {
    //读取域名配置
    @GET()
    fun get(@Url url: String): Observable<String>

    //前端配置
    @POST
    fun config(@Url url: String): Observable<BaseResponse<ConfigData>>
}