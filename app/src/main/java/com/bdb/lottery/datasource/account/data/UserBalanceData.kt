package com.bdb.lottery.datasource.account.data

import android.text.Html
import android.text.Spanned
import com.bdb.lottery.const.PLATFORM
import com.bdb.lottery.extension.h5Color
import com.bdb.lottery.extension.money

data class UserBalanceData(
    val center: Double,
    val todayLossMaps: List<TodayLossMap>,
    val userName: String,
) {
    //经典-今日输赢
    private fun jdTodayProfit(): Double {
        if (todayLossMaps.isNullOrEmpty()) return 0.0
        for (todayLossMap in todayLossMaps) {
            if (todayLossMap.platform != PLATFORM.PLATFORM_JD_ID) continue
            return todayLossMap.profitlossMoneyTotal
        }
        return 0.0
    }

    //经典-今日输赢-h5样式
    fun jdTodayProfitH5(pattern: String): Spanned {
        val profit = jdTodayProfit()
        return Html.fromHtml(
            String.format(
                pattern,
                (profit).money()
                    .h5Color(if (profit > 0) "#FA4529" else if (profit < 0) "#66CC66" else "#333333")
            )
        )
    }

    //传统-今日输赢
    fun trTodayProfit(): Double {
        if (todayLossMaps.isNullOrEmpty()) return 0.0
        for (todayLossMap in todayLossMaps) {
            if (todayLossMap.platform != PLATFORM.PLATFORM_TR_ID) continue
            return todayLossMap.profitlossMoneyTotal
        }
        return 0.0
    }

    //经典-余额
    fun jdAccountBalance(pattern: String): String {
        return String.format(
            pattern,
            center.money()
        )
    }
}

data class TodayLossMap(
    val agentBonusMoney: Double,
    val orderMoneyTotal: Double,
    val platform: Int,
    val profitlossMoneyTotal: Double,
    val selfBonusMoney: Double,
    val winMoneyTotal: Double,
)