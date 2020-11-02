package com.bdb.lottery.biz.main.home

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.game.GameRemoteDs
import com.bdb.lottery.extension.money
import javax.inject.Inject

class HomeViewModel @ViewModelInject @Inject constructor(
    private val accountRemoteDs: AccountRemoteDs,
    private val gameRemoteDs: GameRemoteDs
) : BaseViewModel() {
    val balanceLd = LiveDataWraper<String>()//余额

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

    }

    //轮播图
    fun bannerData() {

    }

    //红包
    fun dividenedAvailable() {

    }

    //红包
    fun receiveDividend() {

    }
}