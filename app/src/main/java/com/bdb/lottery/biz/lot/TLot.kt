package com.bdb.lottery.biz.lot

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bdb.lottery.R
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.utils.game.TGame
import com.bdb.lottery.utils.time.TTime
import com.bdb.lottery.utils.ui.keyboard.KeyBoards
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class TLot @Inject constructor(
    @ActivityContext private val context: Context,
    private val tGame: TGame,
    private val tTime: TTime
) {
    //region 经典：单式输入法适配
    fun adjustSoftInput(listener: KeyBoards.OnSoftInputChangedListener) {
        val window = (context as Activity).window
        KeyBoards.fixAndroidBug5497(window)
        KeyBoards.registerSoftInputChangedListener(window, listener)
    }
    //endregion

    //region 开奖号码列表
    fun openNumsAdapter(gameType: Int, nums: String): TagAdapter<String> {
        val split = nums.split(" ")
        val brightIndexs = tGame.brightIndexs("1", gameType)
        return object : TagAdapter<String>(split) {
            override fun getView(parent: FlowLayout?, position: Int, num: String?): View {
                val textView = TextView(context)
                textView.gravity = Gravity.CENTER
                textView.text = num
                textView.setBackgroundResource(R.drawable.lot_open_nums_white_circle_shape)
                textView.alpha =
                    if (brightIndexs.contains(position + 1)) 1f else 0.6f
                return textView
            }
        }
    }
    //endregion

    //region 倒计时返回：状态、时分秒
    fun countDownData(
        currentTime: CountDownData.CurrentTime,
        lotJdFragment: LotJdFragment?
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

        //更新经典状态
        lotJdFragment?.updateLotStatus(isClosed)
        return if (showHourReal) {
            arrayOf(hour, minute, second)
        } else {
            arrayOf(minute, second)
        }
    }
    //endregion
}