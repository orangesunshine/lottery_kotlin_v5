package com.bdb.lottery.datasource.lot.data.countdown

import java.util.concurrent.ConcurrentHashMap


data class CountDownData(
    var currents: Map<Int, CurrentTime>,
    var futureIssue: Map<Int, List<FutureTime>>,
    var time: Long // 1606103050497
) {
    data class CurrentTime(
        var closeTime: Long, // 2020-11-20 18:08:50 //封盘时间
        var isclose: Boolean, // true
        var issueno: String, // 10744083
        var lotteryTime: Long, // 2020-11-20 18:09:00 //开奖时间
        var now: Long, // 2020-11-23 11:45:12
        var openTime: Long, // 2020-11-20 18:08:00 //开盘时间
    ) {
        var betTotalTime: Long = 0//投注总时间
        var closeTotalTime: Long = 0//封盘总时间
        var totalTime: Long = 0//总时间
        var betSurplusTime: Long = 0//封盘剩余时间
        var openSurplusTime: Long = 0//开奖剩余时间
        var isFinish: Boolean = true//是否结束

        fun initVar() {
            isclose = now > closeTime
            closeTotalTime = lotteryTime - closeTime//封盘总时间
            betTotalTime = closeTime - openTime//投注总时间
            totalTime = lotteryTime - openTime//总时间
            betSurplusTime = if (!isclose) closeTime - now else 0//封盘剩余时间
            openSurplusTime = if (isclose) lotteryTime - now else closeTotalTime//开奖剩余时间
            isFinish = openSurplusTime <= 0
        }

        init {
            initVar()
        }
    }

    data class FutureTime(
        var closeTime: Long, // 1605867510000
        var issueno: String, // 10744085
        var lotteryTime: Long, // 1605867540000
        var openTime: Long // 1605867240000
    ) {
        fun convertCurrent(): CurrentTime {
            return CurrentTime(
                closeTime,
                false,
                issueno,
                lotteryTime,
                openTime,
                openTime
            )
        }
    }

    fun mapper(cache: ConcurrentHashMap<Int, CountDownMapper?>) {
        if (currents.isNullOrEmpty()) return
        currents.forEach {
            val gameId = it.key
            cache[gameId] = CountDownMapper(it.value, futureIssue[gameId])
        }
    }

    data class CountDownMapper(
        var currentTime: CurrentTime?,
        var futureTime: List<FutureTime>?
    ) {

        //下一期
        fun nextIssue() {
            currentTime = null
            futureTime?.let {
                if (it.isNotEmpty()) {
                    currentTime = it.first().convertCurrent()
                    futureTime = it.subList(1, it.size)
                }
            }
        }
    }

    fun initCurrentTime() {
        currents.values.forEach {
            it.initVar()
        }
    }
}

