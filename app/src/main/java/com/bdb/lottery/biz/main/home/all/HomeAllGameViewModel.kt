package com.bdb.lottery.biz.main.home.all

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.R
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.datasource.game.data.HomeFavoritesMapper
import com.bdb.lottery.datasource.game.data.LotteryFavoritesData
import javax.inject.Inject

class HomeAllGameViewModel @ViewModelInject @Inject constructor(
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs
) : BaseViewModel() {
    val favouritesLd = LiveDataWraper<MutableList<HomeFavoritesMapper>?>()

    //全部彩种
    fun allGame() {
        gameRemoteDs.allGame{

        }
    }
}