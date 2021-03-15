package com.bdb.lottery.datasource.game

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.game.data.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.POST

interface GameApi {

    //获取全部游戏
    @POST(URL.URL_ALL_GAME)
    fun allGame(): Observable<BaseResponse<MutableList<AllGameItemData>>>

    //彩种大类每个游戏
    @POST(URL.URL_GAME_BY_GENRES)
    fun gameByGenres(@Field("gameGenres") gameGenres: String): Observable<BaseResponse<List<GenresGameDataItem>>>

    //其他平台
    @POST(URL.URL_OTHER_GAME)
    fun otherGame(): Observable<BaseResponse<OtherPlatformData>>

    //三方平台
    @POST(URL.URL_THIRD_GAME)
    fun thirdGame(@Field("terminal") gameGenres: String = "4"): Observable<BaseResponse<List<ThirdPlatformDataItem>>>

    //收藏
    @POST(URL.URL_LOTTERY_FAVORITES)
    fun lotteryFavorites(): Observable<BaseResponse<LotteryFavoritesData>>
}