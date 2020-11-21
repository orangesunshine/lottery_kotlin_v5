package com.bdb.lottery.datasource.lot.data.jd

data class GameInitData(
    var current: Current,
    var game: Game,
    var oddInfo: List<OddInfo>,
    var singledInfo: SingledInfo,
    var token: String,
    var user: Double,
    var userBonus: Double,
    var userparent: Int
)

data class Current(
    var lotteryNums: LotteryNums
)

data class Game(
    var gameGenres: Int,
    var gamePictureUrl: String,
    var maxwinmoney: Double,
    var name: String,
    var remarks: String,
    var startExplain: Boolean,
    var startSign: Boolean
)

data class OddInfo(
    var limit_money: Double,
    var odds: Double,
    var play_type: Int,
    var special_number: String
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

data class LotteryNums(
    var betmoneytotal: Double,
    var closetime: Long,
    var defaultnums: Any?,
    var gametype: Any?,
    var gametypename: Any?,
    var id: Any?,
    var issueno: String,
    var kjbetmoneytotal: Double,
    var kjorderCounter: Int,
    var kjwinmoneytotal: Double,
    var lotterydt: Long,
    var lotterydt_begin: Any?,
    var lotterydt_end: Any?,
    var nums: Any?,
    var openState: Boolean,
    var opentime: Long,
    var operatingTime: Any?,
    var `operator`: Any?,
    var orderCounter: Int,
    var `param`: Any?,
    var processText: String,
    var remark: Any?,
    var state: Any?,
    var winmoneytotal: Double
)