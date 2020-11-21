package com.bdb.lottery.datasource.lot

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.IUrl
import com.bdb.lottery.datasource.lot.data.*
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.GameInitData
import com.bdb.lottery.datasource.lot.data.kg.KgBetTypeData
import com.bdb.lottery.datasource.lot.data.kg.KgInitData
import com.bdb.lottery.datasource.lot.data.wt.WtBetTypeData
import com.bdb.lottery.datasource.lot.data.wt.WtInitData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LotApi {
    //获取今日开奖（露珠图）
    @POST(IUrl.URL_GET_TODAY_LOTTERY_NUMS)
    @FormUrlEncoded
    fun getTodayLotteryNums(
        @Field("gameId") gameId: String,
        @Field("count") type: String = "1500"
    ): Observable<BaseResponse<TodayLotteryNumsData?>>


    //获取未来期
    @POST(IUrl.URL_GET_BETTING)
    fun getBetting(): Observable<BaseResponse<GetBettingData?>>

    //根据ID获取该彩种历史开奖
    @POST(IUrl.URL_GET_HISTORY)
    fun getHistory(): Observable<BaseResponse<HistoryData?>>

    //region 经典
    //经典彩种初始化
    @POST(IUrl.URL_INIT_GAME)
    fun initGame(): Observable<BaseResponse<List<GameInitData>?>>

    //经典
    @POST(IUrl.URL_GET_BET_TYPE)
    fun getBetType(): Observable<BaseResponse<GameBetTypeData?>>
    //endregion

    //region Kg
    //kg彩种初始化
    @POST(IUrl.URL_INIT_KG_GAME)
    fun initKgGame(): Observable<BaseResponse<KgInitData?>>


    //kg
    @POST(IUrl.URL_GET_KG_BET_TYPE)
    fun getKgBetType(): Observable<BaseResponse<KgBetTypeData?>>
    //endregion


    //region 微投
    //微投彩种初始化
    @POST(IUrl.URL_INIT_WT_GAME)
    fun initWtGame(): Observable<BaseResponse<WtInitData?>>


    //微投
    @POST(IUrl.URL_GET_WT_BET_TYPE)
    fun getWtBetType(): Observable<BaseResponse<WtBetTypeData?>>
    //endregion
}