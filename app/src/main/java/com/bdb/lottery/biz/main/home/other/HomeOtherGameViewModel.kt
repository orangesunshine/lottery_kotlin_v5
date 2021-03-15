package com.bdb.lottery.biz.main.home.other

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.datasource.game.data.OtherGameDataMapper
import javax.inject.Inject

class HomeOtherGameViewModel @ViewModelInject @Inject constructor(
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs,
) : BaseViewModel() {
    val otherGameLd = LiveDataWrapper<MutableList<OtherGameDataMapper>?>()

    //娱乐
    fun otherGame() {
        gameRemoteDs.cachePriOtherGame {
            val mir = it.MIR
            mir?.let {
                appRemoteDs.cachePrePlatformParams { platform: PlatformData? ->
                    mir.asSequence().map {
                        platform?.otherGameImgUrl(it)
                        it
                    }.groupBy { it.subPlatform }.filter { !it.value.isNullOrEmpty() }
                        .run {
                            val datalist = mutableListOf<OtherGameDataMapper>()
                            val iterator = this.iterator()
                            var i = 0
                            var mapper: OtherGameDataMapper? = null
                            while (iterator.hasNext()) {
                                val next = iterator.next()
                                if (i % 2 == 0) {
                                    mapper = OtherGameDataMapper()
                                    datalist.add(mapper)
                                    mapper.leftSubPlatform = next.key
                                    mapper.leftData = next.value
                                } else {
                                    mapper!!.rightSubPlatform = next.key
                                    mapper.rightData = next.value
                                }
                                i++
                            }
                            otherGameLd.setData(datalist)
                        }
                }
            } ?: otherGameLd.setData(null)
        }
    }
}