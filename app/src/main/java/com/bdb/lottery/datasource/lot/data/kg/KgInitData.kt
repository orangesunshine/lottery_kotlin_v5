package com.bdb.lottery.datasource.lot.data.kg

data class KgInitData(
    var game: Game,
    var singledInfo: SingledInfo,
    var token: String,
    var user: Double,
    var userBonus: Double,
    var userparent: Int
)

data class Game(
    var gameGenres: Int,
    var gamePictureUrl: String,
    var kjmaxwinmoney: Double,
    var name: String,
    var numMax: Int,
    var numMin: Int,
    var remarks: String,
    var startExplain: Boolean,
    var startSign: Boolean,
    var sumbigNum: Int
)

data class SingledInfo(
    var id: Int,
    var maxmoney: Double,
    var maxmoney2: Double,
    var maxmoney3: Double,
    var maxmoney4: Double,
    var scales: Double,
    var scales2: Double,
    var scales3: Double,
    var scales4: Double
)