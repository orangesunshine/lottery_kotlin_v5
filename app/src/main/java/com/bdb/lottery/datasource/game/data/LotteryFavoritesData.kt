package com.bdb.lottery.datasource.game.data

import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.Placeholder
import com.bdb.lottery.R
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.extension.isSpace

data class LotteryFavoritesData(
    var defaultList: MutableList<FavoritesGameType>?,
    var gameTypeList: MutableList<FavoritesGameType>?
)

data class FavoritesGameType(
    var collectType: String?,
    var gameGenres: Int?,
    var gameId: String?,
    var gameInfo: GameInfo?,
    var gameName: String?,
    var subPlatform: String?,
    var thirdGameInfo: ThirdGameInfo?
) {
    fun homeMapper(platform: PlatformData?): HomeFavoritesMapper {
        var homeImgUrl: String? = null
        var placeholder = R.drawable.home_img_add_bg
        if (TextUtils.equals("0", collectType)) {
            placeholder = R.drawable.home_img_placeholder_bg
            //彩票
            homeImgUrl = StringBuilder().append(platform?.imgurl)
                .append(platform?.lotteryFileImgSquare)
                .append(gameInfo?.recommendType?.let { if(!it.isSpace())gameId + "_" + it else gameId } ?: gameId)
                .append(".png")
                .append(platform?.imgFlushSuffix).toString()
        } else if (TextUtils.equals("1", collectType)) {
            placeholder = R.drawable.home_img_placeholder_bg
            //第三方游戏
            homeImgUrl = StringBuilder().append(platform?.imgurl)
                .append(thirdGameInfo?.appImgUrlSquare)
                .append("?")
                .append(platform?.imgFlushSuffix).toString()

        }
        return HomeFavoritesMapper(
            placeholder,
            homeImgUrl,
            collectType,
            gameId,
            gameInfo,
            thirdGameInfo
        )
    }
}

data class ThirdGameInfo(
    var id: Int?,
    val name: String?,
    val gameCode: String?,
    val platform: String?,
    val subPlatform: Int?,
    val webImgUrl: String?,
    val h5ImgUrl: String?,
    val appImgUrl: String?,
    val appImgUrlSquare: String?,
    val isOpen: Int?,
    val isHot: Int?,
    val webSupport: Int?,
    val h5Support: Int?,
    val appSupport: Int?,
    val sequence: Int?,
    val isIndex: Int?,
    val isExistPlay: String?,
    val appDisplayMode: String?,
    val thirdPlatId: Int?,
    val gameType: String?,
    val appGameCode: String?,
    val h5GameCode: String?,
    val subPlatformAlias: Int?,
    val isOpenGame: Boolean?,
)

data class GameInfo(
    var deep: Int,
    var gameId: Int,
    var gameKind: Int,
    var hotSort: Int,
    var kgEnable: Boolean,
    var name: String,
    var numMax: Int,
    var numMin: Int,
    var ptEnable: Boolean,
    var recommendSort: Int,
    var recommendType: String?,
    var remark: String,
    var sumbigNum: Int,
    var wtEnable: Boolean
)

data class HomeFavoritesMapper(
    @DrawableRes var placeholder: Int,//默认图片、添加图片
    var homeImgUrl: String?,//home页面收藏、游戏、娱乐图片
    var collectType: String?,
    var gameId: String?,
    var gameInfo: GameInfo?,
    var thirdGameInfo: ThirdGameInfo?,
)