package com.bdb.lottery.datasource.domain

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.datasource.app.data.PlatformData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface DomainApi {
    //验证域名，并获取前端配置
    @POST
    fun urlPlatformParams(@Url url: String): Observable<BaseResponse<PlatformData>>

    //读取域名配置
    @GET()
    fun get(@Url url: String): Observable<String>
}