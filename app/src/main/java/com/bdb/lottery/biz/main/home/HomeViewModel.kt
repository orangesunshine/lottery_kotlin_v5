package com.bdb.lottery.biz.main.home

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.globallivedata.AccountManager
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.cocos.CocosRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.datasource.home.HomeRemoteDs
import com.bdb.lottery.datasource.home.data.BannerMapper
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.event.BalanceEvent
import com.bdb.lottery.extension.isSpace
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class HomeViewModel @ViewModelInject @Inject constructor(
    private val accountRemoteDs: AccountRemoteDs,
    private val homeAppRemoteDs: HomeRemoteDs,
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs,
    private val accountManager: AccountManager,
    private val cocosRemoteDs: CocosRemoteDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    //region eventbus注册、注销--余额事件
    init {
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onBalanceEvent(event: BalanceEvent) {
        netBalance()
    }
    //endregion

    //region 网络数据请求

    //region 预加载：推荐收藏、全部彩票、三方游戏、玩法说明
    fun preCacheCache() {
        gameRemoteDs.preCacheLotteryFavorites()//推荐收藏
        gameRemoteDs.preCacheAllGame()//全部彩票
        gameRemoteDs.preCacheOtherGame()//三方游戏
        lotRemoteDs.refreshHowToPlayCache()//玩法说明
    }
    //endregion

    //region 余额
    fun netBalance() {
        accountRemoteDs.balance {
            accountManager.saveUserBalance(it)
        }
    }
    //endregion

    //region 根据类型查询彩种
    fun getLotteryByType() {

    }
    //endregion

    //region 官方说明
    fun refreshGameOfficialDes() {
        lotRemoteDs.refreshOfficialDescCache()
    }
    //endregion

    //region 快捷转账
    fun fastTransfer() {

    }
    //endregion

    //region 公告
    val noticeLd = LiveDataWrapper<String>()//公告
    fun noticeData() {
        homeAppRemoteDs.notice {
            it.roll.filter {
                val type = it.type
                !type.isSpace() && ("1".equals(type) || "2".equals(type))
            }.map {
                it.content
            }.let {
                noticeLd.setData(it.joinToString { it })

            }
        }
    }
    //endregion

    //region 轮播图
    val bannerLd = LiveDataWrapper<List<BannerMapper>?>()//轮播图
    fun bannerData() {
        homeAppRemoteDs.bannerDatas {
            if (it.list.isNullOrEmpty()) {
                bannerLd.setData(null)
            } else {
                val mapper = { imghost: String ->
                    bannerLd.setData(it.list.map {
                        val remarks = it.remarks
                        val needJump = !it.remarks.isSpace() && remarks.startsWith("@")
                        BannerMapper(
                            imghost + it.imgurl,
                            needJump,
                            if (needJump) remarks.substring(1) else null
                        )
                    })
                }

                val serverUrl = it.serverUrl
                if (serverUrl.isSpace()) {
                    appRemoteDs.cachePrePlatformParams() {
                        mapper(it.imgurl)
                    }
                } else {
                    mapper(serverUrl)
                }
            }
        }
    }
    //endregion

    //region 红包
    //红包开关
    fun dividendAvailable() {

    }

    //领取红包
    fun receiveDividend() {

    }
    //endregion

    //region 下载全部的cocos文件
    fun downloadAllCocosFiles() {
        cocosRemoteDs.downloadAllCocos()
    }
    //endregion
    //endregion
}