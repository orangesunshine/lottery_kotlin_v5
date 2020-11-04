package com.bdb.lottery.biz.main.home.all

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.datasource.game.data.AllGameDataItemData
import com.bdb.lottery.datasource.game.data.AllGameDataMapper
import javax.inject.Inject

class HomeAllGameViewModel @ViewModelInject @Inject constructor(
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs
) : BaseViewModel() {
    val allgameLd = LiveDataWraper<MutableList<AllGameDataMapper>?>()

    //全部彩种
    fun allGame() {
        gameRemoteDs.allGame {
            it?.let {
                it.asSequence().groupBy { it.gameType }.filter { !it.value.isNullOrEmpty() }.run {
                    val list = mutableListOf<AllGameDataMapper>()
                    val iterator = this.iterator()
                    var i = 0
                    var mapper: AllGameDataMapper? = null
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        if (i % 2 == 0) {
                            mapper = AllGameDataMapper()
                            list.add(mapper)
                            mapper.leftGameType = next.key
                            mapper.leftDatas = next.value
                        } else {
                            mapper!!.rightGameType = next.key
                            mapper.rightDatas = next.value
                        }
                        i++
                    }
                    allgameLd.setData(list)
                }
            } ?: allgameLd.setData(null)
        }
    }
}