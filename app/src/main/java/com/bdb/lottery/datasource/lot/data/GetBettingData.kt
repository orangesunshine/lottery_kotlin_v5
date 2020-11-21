package com.bdb.lottery.datasource.lot.data

data class GetBettingData(
    var currents: Map<String, Currents>,
    var futureIssue: Map<String, List<FutureIssue>>,
    var time: Long
)

data class Currents(
    var closeTime: Long,
    var isclose: Boolean,
    var issueno: String,
    var lotteryTime: Long,
    var now: Long,
    var openTime: Long
)

data class FutureIssue(
    var closeTime: Long,
    var issueno: String,
    var lotteryTime: Long,
    var openTime: Long
)