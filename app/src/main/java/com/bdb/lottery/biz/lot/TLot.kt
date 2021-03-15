package com.bdb.lottery.biz.lot

import android.app.Activity
import android.content.Context
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.utils.time.TTime
import com.bdb.lottery.utils.ui.keyboard.KeyBoards
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class TLot @Inject constructor(
    @ActivityContext private val context: Context,
    private val tTime: TTime,
) {
    //region 经典：单式输入法适配
    fun adjustSoftInput(listener: KeyBoards.OnSoftInputChangedListener) {
        val window = (context as Activity).window
        KeyBoards.fixAndroidBug5497(window)
        KeyBoards.registerSoftInputChangedListener(window, listener)
    }
    //endregion

    //region 倒计时返回：状态、时分秒
    fun countDownData(
        currentTime: CountDownData.CurrentTime
    ): Array<String> {
        val isClosed = currentTime.isclose
        val showHour = currentTime.betTotalTime / 1000 / 60 / 60 > 0//是否显示小时
        val surplusTime = tTime.surplusTime2String(
            showHour,
            if (isClosed) currentTime.openSurplusTime else currentTime.betSurplusTime
        )
        val split = surplusTime.split(":")
        val showHourReal = showHour || (split.size > 2)
        val hour = if (split.size > 2) split[0] else "00"
        val minute = if (split.size > 2) split[1] else if (split.size > 1) split[0] else "00"
        val second =
            if (split.size > 2) split[2] else if (split.size > 1) split[1] else if (split.isNotEmpty()) split[0] else "00"
        return if (showHourReal) {
            arrayOf(hour, minute, second)
        } else {
            arrayOf(minute, second)
        }
    }
    //endregion
}