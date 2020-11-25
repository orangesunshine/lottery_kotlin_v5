package com.bdb.lottery.biz.lot

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.TGame
import com.bdb.lottery.utils.TTime
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.lot_activity.*
import javax.inject.Inject

@AndroidEntryPoint
class LotActivity : BaseActivity(R.layout.lot_activity) {
    private val TYPE_FRAGMENT_JD = 0x01//经典
    private val TYPE_FRAGMENT_TR = 0x02//传统
    private val TYPE_FRAGMENT_WT = 0x03//微投

    private val vm by viewModels<LotViewModel>()

    @Inject
    lateinit var tTime: TTime

    override var statusbarLight = false;//状态栏是否半透明
    private var mFragmentType = TYPE_FRAGMENT_JD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.bindService()
        vm.getHistoryByGameId()
    }

    fun switchFragment(type: Int) {
        if (mFragmentType == type) return
        lotNumsLabelFl.visible(TYPE_FRAGMENT_TR == type)
        mFragmentType = type
    }

    override fun observe() {
        vm.countDown.getLiveData().observe(this, { coundown(it) })//倒计时
        vm.curIssue.getLiveData().observe(this, { })//开奖号码
        vm.historyIssue.getLiveData().observe(this, { })//历史开奖号码
    }

    //开奖号码
    private fun curIssueNums(curIssue: HistoryData.HistoryItem?) {
        curIssue?.let {
            lotTopRectIssueTv.text = vm.gameNameNdShortIssue(it.issueno)
            val nums = it.nums
            if (!nums.isSpace()) {
                val split = nums!!.split(" ")
                val brightIndexs = vm.getBrightIndexs("")
                lotOpenNumsFl.adapter =object : TagAdapter<String>(split) {
                    override fun getView(parent: FlowLayout?, position: Int, t: String?): View {

                    }
                }
            }
        }
    }



    //历史开奖号码
    private fun historyIssueNums(historyIssues: List<HistoryData.HistoryItem?>?) {

    }

    //region 倒计时
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


    override fun attachActionBar(): Boolean {
        return false
    }

    override fun attachStatusBar(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.unBindService()
    }
}