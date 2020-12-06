package com.bdb.lottery.biz.lot.jd

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.LotParam
import javax.inject.Inject


class LotJdViewModel @ViewModelInject @Inject constructor(
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    var mToken: String? = null

    fun initGame(gameId: String) {
        lotRemoteDs.initGame(gameId) { mToken = it?.token }
    }

    fun getBetType(gameId: String) {
        lotRemoteDs.getBetType(gameId) { }
    }

    //下注
    fun lot(param: LotParam) {
        lotRemoteDs.lot(param, {}, {})
    }
}