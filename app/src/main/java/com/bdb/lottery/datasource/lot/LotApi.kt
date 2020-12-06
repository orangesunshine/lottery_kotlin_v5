package com.bdb.lottery.datasource.lot

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.LotData
import com.bdb.lottery.datasource.lot.data.TodayLotteryNumsData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
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
    @POST(URL.URL_GET_TODAY_LOTTERY_NUMS)
    @FormUrlEncoded
    fun getTodayLotteryNums(
        @Field("gameId") gameId: String,
        @Field("count") type: String = "1500",
    ): Observable<BaseResponse<TodayLotteryNumsData?>>


    //获取未来期
    @POST(URL.URL_GET_BETTING)
    @FormUrlEncoded
    fun getBetting(
        @Field("gameId") gameIds: String,
        @Field("count") type: String = "120",
    ): Observable<BaseResponse<CountDownData?>>

    //根据ID获取该彩种历史开奖
    @POST(URL.URL_GET_HISTORY)
    @FormUrlEncoded
    fun getHistoryByGameId(
        @Field("gameId") gameId: String,
        @Field("count") type: String = "80",
    ): Observable<BaseResponse<HistoryData?>>

    //region 经典
    //经典彩种初始化
    @POST(URL.URL_INIT_GAME)
    @FormUrlEncoded
    fun initGame(@Field("gameID") gameId: String): Observable<BaseResponse<GameInitData?>>

    //经典
    @POST(URL.URL_GET_BET_TYPE)
    @FormUrlEncoded
    fun getBetType(@Field("gameId") gameId: String): Observable<BaseResponse<GameBetTypeData?>>
    //endregion

    //region Kg
    //kg彩种初始化
    @POST(URL.URL_INIT_KG_GAME)
    @FormUrlEncoded
    fun initKgGame(@Field("gameID") gameId: String): Observable<BaseResponse<KgInitData?>>


    //kg
    @POST(URL.URL_GET_KG_BET_TYPE)
    @FormUrlEncoded
    fun getKgBetType(@Field("gameId") gameId: String): Observable<BaseResponse<KgBetTypeData?>>
    //endregion

    //region 微投
    //微投彩种初始化
    @POST(URL.URL_INIT_WT_GAME)
    @FormUrlEncoded
    fun initWtGame(@Field("gameID") gameId: String): Observable<BaseResponse<WtInitData?>>


    //微投
    @POST(URL.URL_GET_WT_BET_TYPE)
    @FormUrlEncoded
    fun getWtBetType(@Field("gameId") gameId: String): Observable<BaseResponse<WtBetTypeData?>>
    //endregion

    //下注
    @POST(URL.URL_CATHECTIC)
    @FormUrlEncoded
    fun lot(@Field("json") lotParams: String): Observable<BaseResponse<LotData?>>
}