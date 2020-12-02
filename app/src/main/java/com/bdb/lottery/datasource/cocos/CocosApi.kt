package com.bdb.lottery.datasource.cocos

import com.bdb.lottery.datasource.cocos.data.CocosData
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface CocosApi {

    @GET
    @Headers("domainIntercept: false", "headerIntercept:false")
    fun cocosConfig(@Url url: String): Observable<CocosData>

    @GET
    @Streaming
    @Headers("domainIntercept: false", "headerIntercept:false")
    fun downFile(@Url url: String): Observable<ResponseBody>
}