package com.bdb.lottery.biz.lot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.const.IExtra
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.setListOrUpdate
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.TTime
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.actionbar_lot_layout.*
import kotlinx.android.synthetic.main.lot_activity.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LotActivity : BaseActivity(R.layout.lot_activity) {
    override var statusbarLight = false;//状态栏是否半透明

    //region 生命周期 开关service，网络请求
    private val vm by viewModels<LotViewModel>()
    private var mExplContent = 0//0开奖记录、1coco动画、2直播
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsK3 = vm.isK3()
        vm.bindService()
        vm.getHistoryByGameId()
        lotTopLeftAreaLl.setOnClickListener {
            switchExplContent(0)
        }
        lotCupIv.setOnClickListener {
            switchExplContent(1)
        }
    }

    fun switchExplContent(expl2Show: Int) {
        if (mExplContent == expl2Show || !lotExpl.isExpanded) lotExpl.toggle()
        if (mExplContent == expl2Show) return
        lotCocosWv.visibility = if (expl2Show == 1) View.VISIBLE else View.INVISIBLE
        lotHistoryRv.visible(expl2Show == 0)
        mExplContent = expl2Show
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.unBindService()
    }
    //endregion

    //region 切换fragment
    private val TYPE_FRAGMENT_JD = 0x01//经典
    private val TYPE_FRAGMENT_TR = 0x02//传统
    private val TYPE_FRAGMENT_WT = 0x03//微投
    private var mFragmentType = TYPE_FRAGMENT_JD
    fun switchFragment(type: Int) {
        if (mFragmentType == type) return
        lotNumsLabelFl.visible(TYPE_FRAGMENT_TR == type)
        mFragmentType = type
    }
    //endregion

    override fun observe() {
        vm.countDown.getLiveData().observe(this, { coundown(it) })//倒计时
        vm.curIssue.getLiveData().observe(this, { curIssueNums(it) })//开奖号码
        vm.historyIssue.getLiveData().observe(this, { historyIssueNums(it) })//历史开奖号码
    }

    //region 开奖号码
    private fun curIssueNums(curIssue: HistoryData.HistoryItem?) {
        Timber.d(curIssue.toString())
        curIssue?.let {
            lotTopRectIssueTv.text = vm.gameNameNdShortIssue(it.issueno)
            val nums = it.nums
            if (!nums.isSpace()) {
                val split = nums!!.split(" ")
                val brightIndexs = vm.getBrightIndexs("1")
                lotOpenNumsFl.adapter = object : TagAdapter<String>(split) {
                    override fun getView(parent: FlowLayout?, position: Int, num: String?): View {
                        val textView = TextView(this@LotActivity)
                        textView.gravity = Gravity.CENTER
                        textView.text = num
                        textView.setBackgroundResource(R.drawable.lot_open_nums_white_circle_shape)
                        textView.alpha =
                            if (brightIndexs.contains(position + 1)) 1f else 0.6f
                        return textView
                    }
                }
            }
        }
    }
    //endregion

    //region 历史开奖号码
    private var mIsK3 = false//是否快3
    private fun historyIssueNums(historyIssues: List<HistoryData.HistoryItem>?) {
        lotHistoryRv.setListOrUpdate(historyIssues?.toMutableList()) {
            object : BaseQuickAdapter<HistoryData.HistoryItem, BaseViewHolder>(
                R.layout.lot_history_item,
                historyIssues?.toMutableList()
            ) {
                override fun convert(holder: BaseViewHolder, item: HistoryData.HistoryItem) {
                    holder.setText(R.id.lot_history_item_issue_tv, "第" + item.issueno + "期")
                    holder.setVisible(R.id.lot_history_item_divide_view, mIsK3)
                    val nums = item.nums
                    if (!nums.isSpace()) {
                        val split = nums!!.split(" ")
                        val brightIndexs = vm.getBrightIndexs("1")
                        holder.getView<TagFlowLayout>(R.id.lot_history_item_nums_fl).adapter =
                            object : TagAdapter<String>(split) {
                                override fun getView(
                                    parent: FlowLayout?,
                                    position: Int,
                                    num: String?
                                ): View {
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
                }
            }
        }
    }
    //endregion

    //region 倒计时
    @Inject
    lateinit var tTime: TTime
    private fun coundown(currentTime: CountDownData.CurrentTime?) {
        currentTime?.let {
            val isclose = it.isclose
            val issue = vm.shortIssue(it.issueno)
            val status = StringBuilder().append(issue).append("期 ")
                .append(if (isclose) "封盘中" else "受注中")
            lotIssueStatusTv.text = status
            val showHour = it.betTotalTime / 1000 / 60 / 60 > 0
            val surplusTime = tTime.surplusTime2String(
                showHour,
                if (isclose) it.openSurplusTime else it.betSurplusTime
            )
            showCountDown(surplusTime, showHour)
        }
    }

    /**
     * 倒计时
     * @param surplusTime：剩余时间
     * @param showHour：是否显示小时
     */
    private fun showCountDown(surplusTime: String?, showHour: Boolean) {
        if (surplusTime.isSpace()) return
        val split = surplusTime!!.split(":")
        val showHour = showHour or (split.size > 2)
        lotCountdownDotFirstTv.visible(showHour)
        lotCountdownFourthTv.visible(!showHour)
        val hour = if (split.size > 2) split[0] else "00"
        var minite = if (split.size > 2) split[1] else if (split.size > 1) split[0] else "00"
        var second =
            if (split.size > 2) split[2] else if (split.size > 1) split[1] else if (split.isNotEmpty()) split[0] else "00"
        if (showHour) {
            lotCountdownFirstTv.text = hour
            lotCountdownSecondTv.text = minite
            lotCountdownThirdTv.text = second
        } else {
            val miniteArray = minite.toCharArray()
            val miniteTen = if (miniteArray.size > 1) miniteArray[0] else "0"
            val miniteOne =
                if (miniteArray.size > 1) miniteArray[1] else if (miniteArray.isNotEmpty()) miniteArray[0] else "0"
            lotCountdownFirstTv.text = miniteTen.toString()
            lotCountdownSecondTv.text = miniteOne.toString()

            val secondArray = second.toCharArray()
            val secondTen = if (secondArray.size > 1) secondArray[0] else "0"
            val secondOne =
                if (secondArray.size > 1) secondArray[1] else if (secondArray.isNotEmpty()) secondArray[0] else "0"
            lotCountdownThirdTv.text = secondTen.toString()
            lotCountdownFourthTv.text = secondOne.toString()
        }
    }
    //endregion

    //region actbar、statusbar
    override fun attachActionBar(): Boolean {
        return false
    }

    override fun attachStatusBar(): Boolean {
        return false
    }
    //endregion

    companion object {
        fun start(context: Context, gameId: String, gameType: String, gameName: String?) {
            val intent = Intent(context, LotActivity::class.java)
            intent.putExtra(IExtra.ID_GAME_EXTRA, gameId)
            intent.putExtra(IExtra.TYPE_GAME_EXTRA, gameType)
            intent.putExtra(IExtra.NAME_GAME_EXTRA, gameName)
            context.startActivity(intent)
        }
    }
}