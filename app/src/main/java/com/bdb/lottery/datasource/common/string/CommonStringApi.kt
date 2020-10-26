package com.bdb.lottery.datasource.common.string

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface CommonStringApi {

    @GET()
    fun get(@Url url: String): Observable<String>
}