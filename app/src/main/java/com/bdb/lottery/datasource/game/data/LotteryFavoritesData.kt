package com.bdb.lottery.datasource.game.data

data class LotteryFavoritesData(
    var defaultList: Any?,
    var gameTypeList: List<FavoritesGameType>
)

data class FavoritesGameType(
    var collectType: String,
    var gameGenres: Int,
    var gameId: String,
    var gameInfo: GameInfo,
    var gameName: String,
    var subPlatform: String,
    var thirdGameInfo: Any?
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
    var recommendType: String,
    var remark: String,
    var sumbigNum: Int,
    var wtEnable: Boolean
)