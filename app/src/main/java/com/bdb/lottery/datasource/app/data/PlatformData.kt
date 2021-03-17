package com.bdb.lottery.datasource.app.data

import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.datasource.game.data.GameInfo
import com.bdb.lottery.datasource.game.data.OtherPlatformMIR
import com.bdb.lottery.datasource.game.data.ThirdGameInfo
import com.bdb.lottery.extension.isSpace
import timber.log.Timber

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
    //圆形图片
    fun imgRound(gameId: String): String {
        return StringBuilder().append(imgurl)
            .append(lotteryFileImgRound)
            .append(gameId)
            .append(".png")
            .append(imgFlushSuffix).toString()
    }

    //方形图片
    private fun imgSquare(gameInfo: GameInfo?): String {
        return StringBuilder().append(imgurl)
            .append(lotteryFileImgSquare)
            .append(gameInfo?.recommendType?.let { if (!it.isSpace()) gameInfo.gameId.toString() + "_" + it else gameInfo.gameId }
                ?: gameInfo?.gameId)
            .append(".png")
            .append(imgFlushSuffix).toString()
    }

    //拼接imgUrl
    private fun imgUrlAppend(append: String): String {
        return StringBuilder().append(imgurl)
            .append(append).toString()
    }

    //首页--全部游戏
    fun homeAllGameImg(allGameItemData: AllGameItemData?) {
        allGameItemData?.let {
            it.imgUrl = imgRound(it.gameId.toString())
        }
    }

    //首页--娱乐
    fun homeOtherGameImg(other: OtherPlatformMIR?) {
        other?.let { it.imgUrl = imgUrlAppend(it.appImgUrl) }
    }

    //收藏--彩票圆角
    fun homeFavoritesImgUrl(gameInfo: GameInfo?): String {
        return imgSquare(gameInfo)
    }

    //收藏--三方游戏
    fun homeThirdGameImgUrl(thirdGameInfo: ThirdGameInfo?): String {
        return StringBuilder().append(imgurl)
            .append(thirdGameInfo?.appImgUrlSquare)
            .append("?")
            .append(imgFlushSuffix).toString()
    }

    //玩法说明
    fun getHowToPlayUrl(): String {
        return "$imgurl/front/file/appwfsm.js"
    }

    fun getGameOfficialDescUrl(): String {
        return "$imgurl/front/file/gameQustion.js"
    }

}