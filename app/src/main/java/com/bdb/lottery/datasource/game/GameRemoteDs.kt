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
    fun refreshAllGame(success: ((MutableList<AllGameItemData>?) -> Unit)? = null) {
        retrofitWrapper.refreshCache(URL.URL_ALL_GAME, gameApi.allGame(), success)
    }

    fun cachePriAllGame(success: ((MutableList<AllGameItemData>?) -> Unit)? = null) {
        retrofitWrapper.cachePri(URL.URL_ALL_GAME, gameApi.allGame(), success)
    }
    //endregion

    fun gameByGenres(genres: String) {
        retrofitWrapper.observe(gameApi.gameByGenres(genres))
    }

    //region othergame
    fun refreshOtherGameCache() {
        retrofitWrapper.refreshCache(URL.URL_OTHER_GAME, gameApi.otherGame())
    }

    fun cachePriOtherGame(success: (OtherPlatformData?) -> Unit) {
        retrofitWrapper.cachePri(URL.URL_OTHER_GAME, gameApi.otherGame(), success)
    }
    //endregion

    fun thirdGame() {
        retrofitWrapper.observe(gameApi.thirdGame())
    }

    //region 收藏
    fun refreshLotteryFavoritesCache() {
        retrofitWrapper.refreshCache(URL.URL_LOTTERY_FAVORITES, gameApi.lotteryFavorites())
    }

    fun cachePriLotteryFavorites(success: (LotteryFavoritesData?) -> Unit) {
        retrofitWrapper.cachePri(
            URL.URL_LOTTERY_FAVORITES,
            gameApi.lotteryFavorites(),
            success
        )
    }
    //endregion
}