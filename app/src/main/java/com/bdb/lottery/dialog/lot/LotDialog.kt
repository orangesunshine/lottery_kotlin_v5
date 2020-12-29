package com.bdb.lottery.dialog.lot

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.fragment.app.FragmentManager
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.BaseDialog
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.time.TTime
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.lot_dialog.*
import javax.inject.Inject

@ActivityScoped
@AndroidEntryPoint
class LotDialog @Inject constructor() : BaseDialog(R.layout.lot_dialog) {
    private val LOT_DIALOG_TAG = "lot_dialog_tag"
    override var mShowBottomEnable = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lot_dialog_game_name_tv.text = mName//彩票名称
        lot_dialog_status_tv.text = mStatus//投注状态
        lot_dialog_play_name_tv.text = mPlayName//玩法名称
        lot_dialog_nums_tv.text = mNums//号码
        lot_dialog_nums_tv.movementMethod = ScrollingMovementMethod.getInstance();
        lot_dialog_notes_tv.text =
            if (mNoteCount > 0) String.format("%d 注", mNoteCount) else null//注数
        lot_dialog_multiple_tv.text =
            if (!mMultiple.isSpace()) String.format("%s 倍", mMultiple) else null//倍数
        lot_dialog_amount_tv.text = mAmount//总额
        lot_dialog_tips_tv.text = mTips//单挑、限红
        lot_dialog_submit_bt.setOnClickListener { mSubmitCallback?.invoke() }
        lot_dialog_close_iv.setOnClickListener { dismiss() }
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
    fun countdown(currentTime: CountDownData.CurrentTime?) {
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
            lot_dialog_countdown_hour_dot_tv.visible(showHourReal)
            lot_dialog_issue_tv.text = String.format("第%s期", currentTime.issueno)
            lot_dialog_status_tv.text = if (it.isclose) "封盘中" else "受注中"
            if (showHourReal) lot_dialog_countdown_hour_tv.text = hour
            lot_dialog_countdown_minute_tv.text = minute
            lot_dialog_countdown_second_tv.text = second
        }
    }
    //endregion

    //加载cb 的logo
    private var mImgUrl: String? = null
    fun loadLotUrl(url: String): LotDialog {
        mImgUrl = url
        return this
    }

    //cb名称
    private var mName: String? = null
    fun lotName(name: String?): LotDialog {
        mName = name
        return this
    }

    //投注状态
    private var mStatus: String? = null
    fun lotStatus(status: String): LotDialog {
        mStatus = status
        return this
    }

    //投注玩法名称
    private var mPlayName: String? = null
    fun lotPlayName(playName: String?): LotDialog {
        mPlayName = playName
        return this
    }

    //投注号码
    private var mNums: String? = null
    fun lotNums(nums: String?): LotDialog {
        mNums = nums
        return this
    }

    //注数
    private var mNoteCount: Int = 0
    fun lotNotesCount(notesCount: Int): LotDialog {
        mNoteCount = notesCount
        return this
    }

    //倍数
    private var mMultiple: String? = null
    fun lotMultiply(multiply: String): LotDialog {
        mMultiple = multiply
        return this
    }

    //总额
    private var mAmount: String? = null
    fun lotAmount(amount: Double, amountUnit: Int): LotDialog {
        mAmount = String.format("%f ${Converts.unit2String(amountUnit)}", amount)
        return this
    }

    //单挑、限红
    private var mTips: String? = null
    fun lotSingledOutNdLimit(tips: String): LotDialog {
        mTips = tips
        return this
    }

    //确定
    private var mSubmitCallback: (() -> Unit)? = null
    fun submit(submit: () -> Unit): LotDialog {
        mSubmitCallback = submit
        return this
    }

    fun show(manager: FragmentManager) {
        super.show(manager, LOT_DIALOG_TAG)
    }
}