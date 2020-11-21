package com.bdb.lottery.datasource.lot.data.jd

class GameBetTypeData : ArrayList<GameBetTypeItemData>()

data class GameBetTypeItemData(
    var list: List<GameItemLayer1>,
    var name: String,
    var ranking: Int
)

data class GameItemLayer1(
    var list: List<GameItemLayer2>,
    var name: String,
    var ranking: Int
)

data class GameItemLayer2(
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
    var singleMaxMoney: Double
)