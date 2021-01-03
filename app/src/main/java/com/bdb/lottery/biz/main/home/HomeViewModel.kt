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
    val bannerLd = LiveDataWrapper<List<BannerMapper>?>()//轮播图
    val noticeLd = LiveDataWrapper<String>()//公告

    init {
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onBalanceEvent(event: BalanceEvent) {
        getBalance()
    }

    //预加载
    fun refreshCache() {
        gameRemoteDs.refreshLotteryFavoritesCache()//推荐收藏
        gameRemoteDs.refreshAllGame()//全部彩票
        gameRemoteDs.refreshOtherGameCache()//三方游戏
        lotRemoteDs.refreshHowToPlayCache()//玩法说明
    }

    //余额
    fun getBalance() {
        accountRemoteDs.balance {
            it?.let {
                accountManager.saveUserBalance(it)
            }
        }
    }

    //根据类型查询彩种
    fun getLotteryByType() {

    }

    //官方验证
    fun getGameOfficialDes() {
        lotRemoteDs.refreshOfficialDescCache()
    }

    //快捷转账
    fun fastTransfer() {

    }

    //公告
    fun noticeData() {
        homeAppRemoteDs.notice {
            it?.let {
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
    }

    //轮播图
    fun bannerData() {
        homeAppRemoteDs.bannerDatas {
            if (null == it || it.list.isNullOrEmpty()) {
                bannerLd.setData(null)
                return@bannerDatas
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
                        mapper(it?.imgurl ?: "")
                    }
                } else {
                    mapper(serverUrl)
                }
            }
        }
    }

    //红包
    fun dividenedAvailable() {

    }

    //红包
    fun receiveDividend() {

    }

    //下载全部的cocos文件
    fun downloadAllCocosFiles() {
        cocosRemoteDs.downloadAllCocos()
    }
}