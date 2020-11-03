package com.bdb.lottery.datasource.game

import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import javax.inject.Inject

class GameRemoteDs @Inject constructor(
    val retrofitWrapper: RetrofitWrapper,
    private val gameApi: GameApi
) {
    fun initGame() {
        retrofitWrapper.observe(gameApi.initGame())
    }

    fun allGame() {
        retrofitWrapper.observe(gameApi.allGame())
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
}