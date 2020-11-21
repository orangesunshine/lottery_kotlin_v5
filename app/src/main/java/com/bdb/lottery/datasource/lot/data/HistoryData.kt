package com.bdb.lottery.datasource.lot.data

class HistoryData : ArrayList<HistoryDataItem>()

data class HistoryDataItem(
    var issueno: String,
    var lotterydt: Long,
    var nums: String?
)