package com.bdb.lottery.datasource.lot.data.wt

class WtBetTypeData : ArrayList<WtItemLayer1>()

data class WtItemLayer1(
    var list: List<WtItemLayer2>,
    var name: String,
    var ranking: Int
)

data class WtItemLayer2(
    var list: List<WtBetType>,
    var name: String,
    var ranking: Int
)

data class WtBetType(
    var classify: String,
    var digit: String,
    var id: Int,
    var maxbet: Double,
    var minbet: Double,
    var name: String,
    var odds: Double,
    var playTypeId: Int,
    var playTypeName: String,
    var ranking: Int,
    var sixplaytype: Int
)