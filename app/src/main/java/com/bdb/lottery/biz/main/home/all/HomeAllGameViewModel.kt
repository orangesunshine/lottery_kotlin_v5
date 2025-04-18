package com.bdb.lottery.biz.main.home.all

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.datasource.game.data.AllGameDataMapper
import com.bdb.lottery.datasource.game.data.AllGameItemData
import javax.inject.Inject

class HomeAllGameViewModel @ViewModelInject @Inject constructor(
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs,
) : BaseViewModel() {
    val allGameLd = LiveDataWrapper<MutableList<AllGameDataMapper>?>()

    //全部彩种
    fun allGame() {
        gameRemoteDs.cachePriAllGame {
            appRemoteDs.cachePrePlatformParams { platform: PlatformData ->
                it.asSequence().sortedBy {
                    when (it.gameKind) {
                        2 -> 10
                        11 -> 5
                        else -> it.gameKind
                    }
                }.map { data: AllGameItemData ->
                    platform.homeAllGameImg(data)
                    data
                }.groupBy {
                    it.gameType
                }.filter { !it.value.isNullOrEmpty() }
                    .run {
                        val dataList = mutableListOf<AllGameDataMapper>()
                        val iterator = this.iterator()
                        var i = 0
                        var mapper: AllGameDataMapper? = null
                        while (iterator.hasNext()) {
                            val next = iterator.next()
                            if (i % 2 == 0) {
                                mapper = AllGameDataMapper()
                                dataList.add(mapper)
                                mapper.leftGameType = next.key
                                mapper.leftData = next.value
                            } else {
                                mapper!!.rightGameType = next.key
                                mapper.rightData = next.value
                            }
                            i++
                        }
                        allGameLd.setData(dataList)
                    }
            }
        }
    }
}