package com.bdb.lottery.service

import com.bdb.lottery.datasource.lot.data.countdown.CountDownData

interface CountDownCallback {
    fun countDown(currentTime: CountDownData.CurrentTime)
}