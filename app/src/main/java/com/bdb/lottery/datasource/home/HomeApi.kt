package com.bdb.lottery.datasource.home

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.home.data.BannerData
import com.bdb.lottery.datasource.home.data.NoticeData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HomeApi {
    //轮播图
    @POST(URL.URL_HOME_BANNER)
    @FormUrlEncoded
    fun bannerDatas(@Field("type") type: String = "4"): Observable<BaseResponse<BannerData>>

    //通知
    @POST(URL.URL_NOTICE)
    fun notice(): Observable<BaseResponse<NoticeData>>
}