package com.bdb.lottery.biz.main.home.all

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.datasource.game.data.AllGameDataMapper
import com.bdb.lottery.datasource.game.data.AllGameItemData
import timber.log.Timber
import javax.inject.Inject

class HomeAllGameViewModel @ViewModelInject @Inject constructor(
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs,
) : BaseViewModel() {
    val allgameLd = LiveDataWraper<MutableList<AllGameDataMapper>?>()

    //全部彩种
    fun allGame() {
        gameRemoteDs.cacheBeforeAllGame {
            it?.let { list: MutableList<AllGameItemData> ->
                appRemoteDs.cacheBeforePlatformParams { platform: PlatformData? ->
                    list.asSequence().map { data: AllGameItemData ->
                        platform?.lotteryFileImgRound(data)
                        data
                    }.groupBy {
                        it.gameType
                    }.filter { !it.value.isNullOrEmpty() }
                        .run {
                            val datalist = mutableListOf<AllGameDataMapper>()
                            val iterator = this.iterator()
                            var i = 0
                            var mapper: AllGameDataMapper? = null
                            while (iterator.hasNext()) {
                                val next = iterator.next()
                                if (i % 2 == 0) {
                                    mapper = AllGameDataMapper()
                                    datalist.add(mapper)
                                    mapper.leftGameType = next.key
                                    mapper.leftData = next.value
                                } else {
                                    mapper!!.rightGameType = next.key
                                    mapper.rightData = next.value
                                }
                                i++
                            }
                            allgameLd.setData(datalist)
                        }
                }
            } ?: allgameLd.setData(null)
        }
    }
}