package com.bdb.lottery.biz.lot.tr

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import javax.inject.Inject


class LotTrViewModel @ViewModelInject @Inject constructor(
    private val lotRemoteDs: LotRemoteDs,
    private val appRemoteDs: AppRemoteDs
) : BaseViewModel() {

    fun initGame(gameId: Int) {
        lotRemoteDs.initKgGame(gameId.toString()) {

        }
    }

    fun getBetType(gameId: Int) {
        lotRemoteDs.getKgBetType(gameId.toString()) {
        }
    }
}