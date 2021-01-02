package com.bdb.lottery.datasource.account.data

import com.bdb.lottery.const.PLATFORM

data class UserBalanceData(
    val center: Double,
    val todayLossMaps: List<TodayLossMap>,
    val userName: String
) {
    fun getTodayJdProfit(): Double {
        if (todayLossMaps.isNullOrEmpty()) return 0.0
        for (todayLossMap in todayLossMaps) {
            if (todayLossMap.platform != PLATFORM.PLATFORM_JD_ID) continue
            return todayLossMap.profitlossMoneyTotal
        }
        return 0.0
    }
}

data class TodayLossMap(
    val agentBonusMoney: Double,
    val orderMoneyTotal: Double,
    val platform: Int,
    val profitlossMoneyTotal: Double,
    val selfBonusMoney: Double,
    val winMoneyTotal: Double
)