package com.bdb.lottery.biz.lot.jd

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotLocalDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.GameInitData
import javax.inject.Inject


class LotJdViewModel @ViewModelInject @Inject constructor(
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    var mToken: String? = null
    val mGameInitData = LiveDataWraper<GameInitData?>()
    val mGameBetTypeData = LiveDataWraper<GameBetTypeData?>()
    val subPlayMethod = LiveDataWraper<SubPlayMethod?>()

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

    //数据库查找玩法说明
    fun getLotType(playId: Int) {
        subPlayMethod.setData(lotLocalDs.queryBetTypeByPlayId(playId)
            ?.let {
                if (it.size > 1) {
                    it.last { !it.belongto.contains("PK10") }
                } else {
                    if (!it.isNullOrEmpty()) it.last() else null
                }
            })
    }
}