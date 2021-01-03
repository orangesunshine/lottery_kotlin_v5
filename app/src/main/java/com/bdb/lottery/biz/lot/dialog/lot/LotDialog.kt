package com.bdb.lottery.biz.lot.dialog.lot

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.BaseDialog
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.money
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.thread.Threads
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
    private var mCanBet = true//请求过投注不能再次下注
    private val vm by viewModels<LotDialogViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lot_dialog_singled_out_tv.visible(mLotParam.isDanTiao())
        lot_dialog_tips_tv.visible(mLotParam.isDanTiao())
        lot_dialog_game_name_tv.text = mLotParam.gameName//彩票名称
        lot_dialog_play_name_tv.text = mLotParam.getPlayTypeName()//玩法名称
        lot_dialog_nums_tv.text = mLotParam.getNums()//号码
        lot_dialog_nums_tv.movementMethod = ScrollingMovementMethod.getInstance();
        val noteCount = mLotParam.getNoteCount()
        val multiple = mLotParam.getMultiple()
        lot_dialog_notes_tv.text =
            if (noteCount > 0) String.format("%d 注", noteCount) else null//注数
        lot_dialog_multiple_tv.text = String.format("%d 倍", multiple) //倍数
        lot_dialog_amount_tv.text = String.format("%s 元", mLotParam.getAmount().money())//总额
        lot_dialog_tips_tv.text = mDanTiaoTips//单挑、限红
        lot_dialog_close_iv.setOnClickListener {
            dismiss()
        }
        lot_dialog_submit_bt.setOnClickListener {
            if (!mCanBet) {
                dismiss()
                return@setOnClickListener
            }
            vm.lot(mLotParam, {
                mSuccess?.invoke()
                lot_dialog_succuess_tv.visible(true)
                Threads.retrofitUIThreadDelayed({ dismiss() }, 1000)
                mSubmitCallback?.invoke()//清空号码
            }, {
                mError?.invoke(it)
                lot_dialog_bet_content_ll.visible(false)
                lot_dialog_error_ll.visible(true)
                lot_dialog_submit_bt.visible(true)
            }, {
                loading()
                lot_dialog_error_ll.visible(false)
                lot_dialog_submit_bt.visible(false)
            }, {
                dismissLoading()
                mCanBet = false
            })
        }
        lot_dialog_close_iv.setOnClickListener { dismissLoading() }
    }

    private fun loading() {
        lot_dialog_loading_success_ll.visible(true)
        lot_dialog_loading_success_iv.setImageResource(R.drawable.lot_dialog_bet_loading)
        lot_dialog_loading_success_iv.startAnimation(AnimationUtils.loadAnimation(
            context,
            R.anim.rotate_anim
        ).apply { interpolator = LinearInterpolator() })
    }

    private fun dismissLoading() {
        lot_dialog_loading_success_ll.visible(false)
        lot_dialog_loading_success_iv.clearAnimation()
    }

    private lateinit var mLotParam: LotParam
    fun lotParams(lotParam: LotParam): LotDialog {
        mLotParam = lotParam
        return this
    }

    private var mSuccess: (() -> Unit)? = null
    fun lotSuccess(success: (() -> Unit)? = null): LotDialog {
        mSuccess = success
        return this
    }

    private var mError: ((String) -> Unit)? = null
    fun lotError(error: ((String) -> Unit)? = null): LotDialog {
        mError = error
        return this
    }

    private var mDanTiaoTips: String? = null
    fun setDanTiaoTips(tips: String?): LotDialog {
        mDanTiaoTips = tips
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

    //确定
    private var mSubmitCallback: (() -> Unit)? = null
    fun submit(submit: () -> Unit): LotDialog {
        mSubmitCallback = submit
        return this
    }

    fun show(manager: FragmentManager) {
        super.show(manager, LOT_DIALOG_TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCanBet = true
        lot_dialog_loading_success_iv.clearAnimation()
        Threads.removeUiThreadCallbacksAndMessages()
    }
}