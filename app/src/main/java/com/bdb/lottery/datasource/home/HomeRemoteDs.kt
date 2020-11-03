package com.bdb.lottery.datasource.home

import com.bdb.lottery.datasource.home.data.BannerData
import com.bdb.lottery.datasource.home.data.NoticeData
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import javax.inject.Inject

class HomeRemoteDs @Inject constructor(
    val retrofitWrapper: RetrofitWrapper,
    private val homeApi: HomeApi
) {
    //轮播图
    fun bannerDatas(success: (BannerData?) -> Unit) {
        retrofitWrapper.observe(homeApi.bannerDatas(), success)
    }

    //通知
    fun notice(success: (NoticeData?) -> Unit) {
        retrofitWrapper.observe(homeApi.notice(), success)
    }
}