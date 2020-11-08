package com.bdb.lottery.datasource.app.data

import com.bdb.lottery.datasource.game.data.AllGameItemData

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
    fun lotteryFileImgRound(allGameItemData: AllGameItemData) {
        allGameItemData.imgUrl = StringBuilder().append(imgurl)
            .append(lotteryFileImgRound)
            .append(allGameItemData.gameId)
            .append(".png")
            .append(imgFlushSuffix).toString()
    }
}