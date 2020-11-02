package com.bdb.lottery.datasource.game.data

data class GenresGameDataItem(
    var currentLotterNums: CurrentLotterNums?,
    var gameType: FavoritesGameType,
    var historyLotteryNums: HistoryLotteryNums?
)

data class CurrentLotterNums(
    var closetime: Long,
    var gametype: Int,
    var isadvance: Int,
    var issueno: String,
    var lotterydt: Long,
    var nums: Any?,
    var opentime: Long,
    var state: Int
)

data class GameType(
    var bonusdiff: Double,
    var closebegindt: Int,
    var closeenddt: Int,
    var deep: Int,
    var digits: Int,
    var enabled: Boolean,
    var gameGenres: Int,
    var gamePictureUrl: String,
    var hotSort: Any?,
    var id: Int,
    var kjmaxwinmoney: Double,
    var maxwinmoney: Double,
    var name: String,
    var noGroup: Int,
    var numMax: Int,
    var numMin: Int,
    var recommendSort: Int,
    var recommendType: String,
    var remarks: String,
    var self: Int,
    var showtype: Any?,
    var startExplain: Boolean,
    var startKj: Int,
    var startSign: Boolean,
    var startWt: Int,
    var sumbigNum: Int
)

data class HistoryLotteryNums(
    var closetime: Long,
    var gametype: Int,
    var isadvance: Int,
    var issueno: String,
    var lotterydt: Long,
    var nums: String,
    var opentime: Long,
    var state: Int
)