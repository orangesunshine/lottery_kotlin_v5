package com.bdb.lottery.datasource.game

import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.datasource.game.data.LotteryFavoritesData
import com.bdb.lottery.datasource.game.data.OtherPlatformData
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import javax.inject.Inject

class GameRemoteDs @Inject constructor(
    val retrofitWrapper: RetrofitWrapper,
    private val gameApi: GameApi,
) {
    fun initGame() {
        retrofitWrapper.observe(gameApi.initGame())
    }

    //region 全部Game
    fun preAllGame(success: ((MutableList<AllGameItemData>?) -> Unit)? = null) {
        retrofitWrapper.preload(HttpConstUrl.URL_ALL_GAME, gameApi.allGame(), success)
    }

    fun cachePriAllGame(success: ((MutableList<AllGameItemData>?) -> Unit)? = null) {
        retrofitWrapper.cachePriLoad(HttpConstUrl.URL_ALL_GAME, gameApi.allGame(), success)
    }
    //endregion

    fun gameByGenres(genres: String) {
        retrofitWrapper.observe(gameApi.gameByGenres(genres))
    }

    //region othergame
    fun preOtherGame() {
        retrofitWrapper.preload(HttpConstUrl.URL_OTHER_GAME, gameApi.otherGame())
    }

    fun cachePriOtherGame(success: (OtherPlatformData?) -> Unit) {
        retrofitWrapper.cachePriLoad(HttpConstUrl.URL_OTHER_GAME, gameApi.otherGame(), success)
    }
    //endregion

    fun thirdGame() {
        retrofitWrapper.observe(gameApi.thirdGame())
    }

    //region 收藏
    fun preLotteryFavorites() {
        retrofitWrapper.preload(HttpConstUrl.URL_LOTTERY_FAVORITES, gameApi.lotteryFavorites())
    }

    fun cachePriLotteryFavorites(success: (LotteryFavoritesData?) -> Unit) {
        retrofitWrapper.cachePriLoad(
            HttpConstUrl.URL_LOTTERY_FAVORITES,
            gameApi.lotteryFavorites(),
            success
        )
    }
    //endregion
}