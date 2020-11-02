package com.bdb.lottery.datasource.game.data

data class OtherPlatformData(
    var MIR: List<OtherPlatformMIR>
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
)