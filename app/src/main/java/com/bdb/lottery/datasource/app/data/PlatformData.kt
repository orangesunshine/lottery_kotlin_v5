package com.bdb.lottery.datasource.app.data

import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.datasource.game.data.GameInfo
import com.bdb.lottery.datasource.game.data.OtherPlatformMIR
import com.bdb.lottery.datasource.game.data.ThirdGameInfo
import com.bdb.lottery.extension.isSpace

data class PlatformData(
    var WebMobileUrl: String,
    var androidAppDownloadUrl: String,
    var androidH5AppDownloadUrl: String,
    var autoRegEnable: String,
    var chatUrl: String,
    var cloudUrl: String,
    var defaultPassword: String,
    var enableDividend: String,
    var enalbeChatRoom: Boolean,
    var imgFlushSuffix: String,
    var imgurl: String,
    var iosAppDownloadUrl: String,
    var iosH5AppDownloadUrl: String,
    var isInvite: String,
    var isOpenInterest: String,
    var isOpenReg: String,
    var isOpenVip: String,
    var isProxyReg: String,
    var isShowReg: String,
    var lotteryFileImgRound: String,
    var lotteryFileImgSquare: String,
    var oldWebUrl: String,
    var pcAppDownloadUrl: String,
    var pictureUrl: String,
    var platform: String,
    var platformName: String,
    var queryDateLimit: String,
    var regEmailConfig: String,
    var regEnable: String,
    var regMobileConfig: String,
    var regQQConfig: String,
    var rsaPublicKey: String,
    var searchDay: String,
    var shiWanEnable: String,
    var sysDefaultRegCode: String,
    var sysMaxRebate: Double,
    var sysMinRebate: Double,
    var userHeadImgNum: String,
    var verifyCapitalPassword: String,
    var wtChatWebConfig: String,
) {
    //彩票圆形
    fun lotteryFileImgRound(allGameItemData: AllGameItemData?) {
        allGameItemData?.let {
            it.imgUrl = StringBuilder().append(imgurl)
                .append(lotteryFileImgRound)
                .append(it.gameId)
                .append(".png")
                .append(imgFlushSuffix).toString()
        }
    }

    //彩票圆角
    fun lotteryFileImgSquare(gameInfo: GameInfo?): String {
        return StringBuilder().append(imgurl)
            .append(lotteryFileImgSquare)
            .append(gameInfo?.recommendType?.let { if (!it.isSpace()) gameInfo.gameId.toString() + "_" + it else gameInfo.gameId }
                ?: gameInfo?.gameId)
            .append(".png")
            .append(imgFlushSuffix).toString()
    }

    //三方游戏
    fun thirdGameImgUrl(thirdGameInfo: ThirdGameInfo?): String {
        return StringBuilder().append(imgurl)
            .append(thirdGameInfo?.appImgUrlSquare)
            .append("?")
            .append(imgFlushSuffix).toString()
    }

    //娱乐
    fun otherGameImgUrl(other: OtherPlatformMIR?) {
        other?.let {
            it.imgUrl = StringBuilder().append(imgurl)
                .append(it.appImgUrl).toString()
        }
    }
}