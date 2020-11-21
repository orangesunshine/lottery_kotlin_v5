package com.bdb.lottery.datasource.lot.data.kg

class KgBetTypeData : ArrayList<KgItemLayer1>()

data class KgItemLayer1(
    var list: List<KgItemLayer2>,
    var name: String,
    var ranking: Int
)

data class KgItemLayer2(
    var list: List<KgItem>,
    var name: String,
    var ranking: Int
)

data class KgItem(
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