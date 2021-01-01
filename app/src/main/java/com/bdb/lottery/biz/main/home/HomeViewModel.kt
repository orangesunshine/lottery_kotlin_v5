package com.bdb.lottery.biz.main.home

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.cocos.CocosRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.datasource.home.HomeRemoteDs
import com.bdb.lottery.datasource.home.data.BannerMapper
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.money
import javax.inject.Inject

class HomeViewModel @ViewModelInject @Inject constructor(
    private val accountRemoteDs: AccountRemoteDs,
    private val homeAppRemoteDs: HomeRemoteDs,
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs,
    private val cocosRemoteDs: CocosRemoteDs
) : BaseViewModel() {
    val balanceLd = LiveDataWrapper<String>()//余额
    val bannerLd = LiveDataWrapper<List<BannerMapper>?>()//轮播图
    val noticeLd = LiveDataWrapper<String>()//公告

    //预加载
    fun preload() {
        gameRemoteDs.preLotteryFavorites()
        gameRemoteDs.preAllGame()
        gameRemoteDs.preOtherGame()
    }

    //余额
    fun getBalance() {
        accountRemoteDs.balance { it?.let { balanceLd.setData(it.center.money()) } }
    }

    //根据类型查询彩种
    fun getLotteryByType() {

    }

    //试玩
    fun trialPlay() {

    }

    //获取玩法说明地址
    fun getPlayDesAddr() {

    }

    //玩法说明
    fun getGameOfficialDes() {

    }

    //获取三方平台
    fun getThirdPlatform() {

    }

    //获取其他平台
    fun getOtherGamePlatform() {

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
                    appRemoteDs.cachePriPlatformParams() {
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