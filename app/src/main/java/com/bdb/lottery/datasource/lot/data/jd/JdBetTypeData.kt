package com.bdb.lottery.datasource.lot.data.jd

class GameBetTypeData : ArrayList<PlayItem>()

data class PlayItem(
    var list: List<PlayGroupItem>?,
    var name: String,
    var ranking: Int,
)

data class PlayGroupItem(
    var list: List<BetItem>?,
    var name: String,
    var ranking: Int,
)

data class BetItem(
    var baseScale: Double,
    var betLossId: Int,
    var betName: String,
    var betType: Int,
    var bonusdiff: Double,
    var classify: String,
    var `data`: String,
    var gameTypeId: Int,
    var id: Int,
    var maxBetCount: Int,
    var multiple: Double,
    var pattern: String,
    var playType: Int,
    var ranking: Int,
    var rootName: String,
    var singleMaxBetCount: Int,
    var singleMaxMoney: Double,
) {
    //玩法title
    fun getJdPlayTitle(): String {
        return "$rootName · $betName";
    }
}