package com.bdb.lottery.datasource.string

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface StringApi {

    @GET()
    fun get(@Url url: String): Observable<String>?
}