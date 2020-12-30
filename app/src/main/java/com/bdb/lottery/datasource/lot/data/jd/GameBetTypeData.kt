package com.bdb.lottery.datasource.lot.data.jd

class GameBetTypeData : ArrayList<PlayLayer1Item>()

data class PlayLayer1Item(
    var list: List<PlayGroupItem>?,
    var name: String,
    var ranking: Int,
)

data class PlayGroupItem(
    var list: List<PlayLayer2Item>?,
    var name: String,
    var ranking: Int,
)

data class PlayLayer2Item(
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
    fun getPlayTitle(): String {
        return "$rootName · $betName";
    }
}