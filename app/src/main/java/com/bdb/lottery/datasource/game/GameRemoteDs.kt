package com.bdb.lottery.datasource.game

import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.datasource.game.data.LotteryFavoritesData
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import javax.inject.Inject

class GameRemoteDs @Inject constructor(
    val retrofitWrapper: RetrofitWrapper,
    private val gameApi: GameApi
) {
    fun initGame() {
        retrofitWrapper.observe(gameApi.initGame())
    }

    fun allGame(success: (MutableList<AllGameItemData>?) -> Unit) {
        retrofitWrapper.observe(gameApi.allGame(), success)
    }

    fun gameByGenres(genres: String) {
        retrofitWrapper.observe(gameApi.gameByGenres(genres))
    }

    fun otherPlatform() {
        retrofitWrapper.observe(gameApi.otherPlatform())
    }

    fun thirdPlatform() {
        retrofitWrapper.observe(gameApi.thirdPlatform())
    }

    fun getLotteryFavorites(success: (LotteryFavoritesData?) -> Unit) {
        retrofitWrapper.observe(gameApi.getLotteryFavorites(), success)
    }
}