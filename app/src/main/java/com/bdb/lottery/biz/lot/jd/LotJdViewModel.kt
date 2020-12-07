package com.bdb.lottery.biz.lot.jd

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.GameInitData
import javax.inject.Inject


class LotJdViewModel @ViewModelInject @Inject constructor(
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    var mToken: String? = null
    val mGameInitData = LiveDataWraper<GameInitData?>()
    val mGameBetTypeData = LiveDataWraper<GameBetTypeData?>()

    fun initGame(gameId: String) {
        lotRemoteDs.initGame(gameId) {
            mGameInitData.setData(it)
            mToken = it?.token
        }
    }

    fun getBetType(gameId: String) {
        lotRemoteDs.getBetType(gameId) {
            mGameBetTypeData.setData(it)
        }
    }
}