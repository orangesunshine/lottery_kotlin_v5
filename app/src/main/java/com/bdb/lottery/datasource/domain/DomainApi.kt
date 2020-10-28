package com.bdb.lottery.datasource.domain

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface DomainApi {
    //读取域名配置
    @GET()
    fun get(@Url url: String): Observable<String>
}