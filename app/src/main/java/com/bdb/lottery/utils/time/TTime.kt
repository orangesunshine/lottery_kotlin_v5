package com.bdb.lottery.utils.time

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTime @Inject constructor() {
    /**
     * 剩余时间转string
     */
    fun surplusTime2String(showHour: Boolean, surplusTime: Long): String {
        return Times.mills2hms(showHour, surplusTime)
    }
}