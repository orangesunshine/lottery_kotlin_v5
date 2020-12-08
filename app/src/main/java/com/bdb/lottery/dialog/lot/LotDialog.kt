package com.bdb.lottery.dialog.lot

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.BaseDialog
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.time.TTime
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.lot_activity.*
import kotlinx.android.synthetic.main.lot_dialog.*
import javax.inject.Inject

@ActivityScoped
class LotDialog @Inject constructor() : BaseDialog(R.layout.lot_dialog) {
    private val LOT_DIALOG_TAG = "lot_dialog_tag"
    private val vm by viewModels<LotDialogViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mShowBottomEnable = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lot_dialog_close_iv.setOnClickListener { dismiss() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.bindService(mGameId)
        vm.countDown.getLiveData().observe(this, { })//倒计时
        lot_dialog_game_name_tv.text = mLotName
        lot_dialog_status_tv.text = mLotStatus
        if (!mLotMultiple.isSpace())
            lot_dialog_multiple_tv.text = String.format("%s 倍", mLotMultiple)
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.unBindService(mGameId)
    }

    //设置gameId
    private var mGameId: Int = -1
    fun gameId(gameId: Int): LotDialog {
        mGameId = gameId
        return this
    }

    //region 倒计时
    @Inject
    lateinit var tTime: TTime
    private fun countdown(currentTime: CountDownData.CurrentTime?) {
        currentTime?.let {
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

            lot_dialog_countdown_hour_tv.visible(showHourReal)
            lot_dialog_countdown_hour_dot_tv.visible(!showHourReal)
            lot_dialog_issue_tv.text = String.format("第%s期", currentTime.issueno)
            lot_dialog_status_tv.text = if (it.isclose) "封盘中" else "受注中"
            if (showHourReal) lot_dialog_countdown_hour_tv.text = hour
            lot_dialog_countdown_minute_tv.text = minute
            lot_dialog_countdown_second_tv.text = second
        }
    }
    //endregion

    //加载cb 的logo
    private var mLotUrl: String? = null
    fun loadLotUrl(url: String): LotDialog {
        mLotUrl = url
        return this
    }

    //cb名称
    private var mLotName: String? = null
    fun lotName(name: String?): LotDialog {
        mLotName = name
        return this
    }

    //投注状态
    private var mLotStatus: String? = null
    fun lotStatus(status: String): LotDialog {
        mLotStatus = status
        return this
    }

    //投注玩法名称
    fun lotPlayName(playName: String): LotDialog {
        lot_dialog_play_name_tv.text = playName
        return this
    }

    //投注号码
    fun lotNums(nums: String): LotDialog {
        lot_dialog_nums_tv.text = nums
        return this
    }

    //注数
    fun lotNotesCount(notesCount: Int) {
        lot_dialog_notes_tv.text = String.format("%d 注", notesCount)
    }

    //倍数
    private var mLotMultiple: String? = null
    fun lotMultiply(multiply: String): LotDialog {
        mLotMultiple = multiply
        return this
    }

    //总额
    fun lotAmount(amount: Double): LotDialog {
        lot_dialog_amount_tv.text = String.format("%f 元", amount)
        return this
    }

    //单挑、限红
    fun lotSingledOutNdLimit(tips: String): LotDialog {
        lot_dialog_tips_tv.text = tips
        return this
    }

    //确定
    fun submit(submit: () -> Unit): LotDialog {
        lot_dialog_submit_bt.setOnClickListener { submit() }
        return this
    }

    fun show(manager: FragmentManager) {
        super.show(manager, LOT_DIALOG_TAG)
    }

}