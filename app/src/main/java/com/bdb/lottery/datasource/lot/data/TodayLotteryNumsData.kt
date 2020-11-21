package com.bdb.lottery.datasource.lot.data

data class TodayLotteryNumsData(
    var MIR: List<Any>,
    var MIR2: List<MIR2>,
    var multipleNumTarget: Int,
    var singleNumTarget: Int
)

data class MIR2(
    var issueno: String,
    var lotterydt: Long,
    var nums: String
)