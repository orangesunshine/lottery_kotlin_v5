package com.bdb.lottery.datasource.game.data

import com.bdb.lottery.R

data class OtherPlatformData(
    var MIR: List<OtherPlatformMIR>?
)

data class OtherPlatformMIR(
    var appDisplayMode: Int,
    var appGameCode: Any?,
    var appImgUrl: String,
    var appImgUrlSquare: String,
    var appSupport: Int,
    var gameCode: String,
    var h5GameCode: Any?,
    var h5ImgUrl: String,
    var h5Support: Int,
    var id: Int,
    var isExistPlay: String,
    var isHot: Int?,
    var isIndex: Int,
    var isOpen: Int,
    var name: String,
    var platform: String,
    var sequence: Int,
    var subPlatform: Int,
    var webImgUrl: String,
    var webSupport: Int
){
    var imgUrl = ""
}

class OtherGameDataMapper {
    var leftSubPlatform: Int = -1
    var leftData: List<OtherPlatformMIR>? = null
    var rightSubPlatform: Int = -1
    var rightData: List<OtherPlatformMIR>? = null

    fun leftSubPlatformDrawable(): Int{
        return subPlatformDrawable(leftSubPlatform)
    }

    fun rightSubPlatformDrawable(): Int{
        return subPlatformDrawable(rightSubPlatform)
    }

    fun subPlatformDrawable(subPlatform: Int): Int {
        var drawable: Int = R.drawable.home_placeholder_round_img_ic
        when (subPlatform) {
            2 -> drawable = R.drawable.home_other_game_electronic
            3 -> drawable = R.drawable.home_other_game_sports
            4 -> drawable = R.drawable.home_other_game_chess
            6 -> drawable = R.drawable.home_other_game_live
            7 -> drawable = R.drawable.home_other_game_fishing
            8 -> drawable = R.drawable.home_other_game_gaming
        }
        return drawable
    }
    override fun toString(): String {
        return "AllGameDataMapper(leftGameType=$leftSubPlatform, leftData=$leftData, rightGameType=$rightSubPlatform, rightData=$rightData)"
    }
}