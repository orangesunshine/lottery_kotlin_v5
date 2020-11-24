package com.bdb.lottery.datasource.lot.data


class HistoryData : ArrayList<HistoryData.HistoryItem>(){
    data class HistoryItem(
        var issueno: String, // 20201124-0238
        var lotterydt: Long, // 1606204680000
        var nums: String? // 2 0 5 8 5
    )
}