package com.bdb.lottery.utils

import java.util.Calendar.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTime @Inject constructor() {
    /**
     * 剩余时间转string
     */
    fun surplusTime2String(showHour: Boolean, surplusTime: Long): String {
        val calendar = getInstance()
        calendar.timeInMillis = surplusTime
        val ret = StringBuilder()
        if (showHour) {
            val hour = calendar.get(HOUR_OF_DAY)
            if (hour < 10) ret.append("0")
            ret.append(hour).append(":")
        }

        val minute = calendar.get(MINUTE)
        if (minute < 10) ret.append("0")
        ret.append(minute).append(":")

        val second = calendar.get(SECOND)
        if (second < 10) ret.append("0")
        ret.append(second)
        return ret.toString()
    }
}