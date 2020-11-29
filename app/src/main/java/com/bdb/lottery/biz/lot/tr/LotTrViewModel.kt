package com.bdb.lottery.biz.lot.tr

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


class LotTrViewModel @ViewModelInject @Inject constructor(
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs
) : BaseViewModel() {
    val favouritesLd = LiveDataWraper<MutableList<HomeFavoritesMapper>?>()

    //获取收藏列表
    fun getLotteryFavourites() {
        gameRemoteDs.cachePriLotteryFavorites {
            //加号➕
            val collectionEnter = HomeFavoritesMapper(
                R.drawable.home_img_add_bg,
                null,
                "-1",
                null,
                null,
                null,
                null,
                null
            )
            it?.let { data: LotteryFavoritesData ->
                appRemoteDs.cachePriPlatformParams { platform: PlatformData? ->
                    favouritesLd.setData(
                        (data.gameTypeList ?: data.defaultList)?.map { it.homeMapper(platform) }
                            ?.toMutableList()?.apply { add(collectionEnter) } ?: mutableListOf(
                            collectionEnter
                        )
                    )
                }
            } ?: favouritesLd.setData(mutableListOf(collectionEnter))
        }
    }
}