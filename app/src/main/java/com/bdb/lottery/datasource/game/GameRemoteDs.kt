package com.bdb.lottery.datasource.game

import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.datasource.game.data.LotteryFavoritesData
import com.bdb.lottery.datasource.game.data.OtherPlatformData
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import javax.inject.Inject

class GameRemoteDs @Inject constructor(
    val retrofitWrapper: RetrofitWrapper,
    private val gameApi: GameApi,
) {
    //region 全部Game
    fun preCacheAllGame(success: ((MutableList<AllGameItemData>) -> Unit)? = null) {
        retrofitWrapper.preCache(URL.URL_ALL_GAME, gameApi.allGame(), success)
    }

    fun cachePriAllGame(success: ((MutableList<AllGameItemData>) -> Unit)? = null) {
        retrofitWrapper.cachePri(URL.URL_ALL_GAME, gameApi.allGame(), success)
    }
    //endregion

    //region 三方游戏
    fun preCacheOtherGame() {
        retrofitWrapper.preCache(URL.URL_OTHER_GAME, gameApi.otherGame())
    }

    fun cachePriOtherGame(success: (OtherPlatformData) -> Unit) {
        retrofitWrapper.cachePri(URL.URL_OTHER_GAME, gameApi.otherGame(), success)
    }
    //endregion

    fun thirdGame() {
        retrofitWrapper.observe(gameApi.thirdGame())
    }

    //region 收藏
    fun preCacheLotteryFavorites() {
        retrofitWrapper.preCache(URL.URL_LOTTERY_FAVORITES, gameApi.lotteryFavorites())
    }

    fun cachePriLotteryFavorites(success: (LotteryFavoritesData) -> Unit) {
        retrofitWrapper.cachePri(
            URL.URL_LOTTERY_FAVORITES,
            gameApi.lotteryFavorites(),
            success
        )
    }
    //endregion

    //region 根据彩种大类获取游戏列表
    fun gameByGenres(genres: String) {
        retrofitWrapper.observe(gameApi.gameByGenres(genres))
    }
    //endregion
}