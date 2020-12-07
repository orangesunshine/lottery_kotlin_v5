package com.bdb.lottery.utils.time

import java.text.SimpleDateFormat
import java.util.*

object Times {
    /**
     * 剩余时间转string
     */
    fun mills2hms(showHour: Boolean, millis: Long): String {
        val ret = StringBuilder()
        val hour = millis / 1000 / 60 / 60
        var residue: Long = millis % (1000 * 60 * 60)
        if (showHour) {
            if (hour < 10) ret.append("0")
            ret.append(hour).append(":")
        }

        val minute = residue / 1000 / 60
        residue %= 1000 * 60
        if (minute < 10) ret.append("0")
        ret.append(minute).append(":")

        val second = residue / 1000
        if (second < 10) ret.append("0")
        ret.append(second)
        return ret.toString()
    }


    //region 格式化日期：时分秒
    fun formatHMS(timeStamp: Long): String {
        return formatHMS(Date(timeStamp))
    }

    fun formatHMS(date: Date): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date)
    }
    //endregion


    //region 格式化日期
    fun formatDate(pattern: String, timeStamp: Long): String {
        return formatDate(pattern, Date(timeStamp))
    }

    fun formatDate(pattern: String, date: Date): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
    //endregion
}