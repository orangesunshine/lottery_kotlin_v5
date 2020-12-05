package com.bdb.lottery.biz.lot

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.biz.lot.tr.LotTrFragment
import com.bdb.lottery.biz.lot.wt.LotWtFragment
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.cocos.TCocos
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.margin
import com.bdb.lottery.extension.setListOrUpdate
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.game.Games
import com.bdb.lottery.utils.game.TGame
import com.bdb.lottery.utils.time.TTime
import com.bdb.lottery.utils.ui.keyboard.KeyBoards
import com.bdb.lottery.utils.ui.size.Sizes
import com.bdb.lottery.utils.ui.size.TSize
import com.sunfusheng.marqueeview.MarqueeView
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.actionbar_lot_layout.*
import kotlinx.android.synthetic.main.lot_activity.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LotActivity : BaseActivity(R.layout.lot_activity) {
    private val JD_FRAGMENT_TAG: String = "JD_FRAGMENT_TAG"
    private val TR_FRAGMENT_TAG: String = "TR_FRAGMENT_TAG"
    private val WT_FRAGMENT_TAG: String = "WT_FRAGMENT_TAG"

    override var statusbarLight = false;//状态栏是否半透明

    @Inject
    lateinit var tSize: TSize

    @Inject
    lateinit var tGame: TGame

    @Inject
    lateinit var tCocos: TCocos

    //region 生命周期 开关service，网络请求
    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    private lateinit var fragments: Array<Fragment>
    private val tags: Array<String> = arrayOf(
        JD_FRAGMENT_TAG,
        TR_FRAGMENT_TAG,
        WT_FRAGMENT_TAG
    )

    override fun initVar(bundle: Bundle?) {
        super.initVar(bundle)
        mGameId = intent.getIntExtra(EXTRA.ID_GAME_EXTRA, -1)
        mGameType = intent.getIntExtra(EXTRA.TYPE_GAME_EXTRA, -1)
        mGameName = intent.getStringExtra(EXTRA.NAME_GAME_EXTRA)
        if (-1 == mGameId || -1 == mGameType) {
            finish()
        }
        fragments = if (null == bundle) {
            arrayOf(LotJdFragment(), LotTrFragment(), LotWtFragment())
        } else {
            arrayOf(
                supportFragmentManager.findFragmentByTag(tags[0]) ?: LotJdFragment(),
                supportFragmentManager.findFragmentByTag(tags[1]) ?: LotTrFragment(),
                supportFragmentManager.findFragmentByTag(tags[2]) ?: LotWtFragment()
            )
        }
    }

    private val vm by viewModels<LotViewModel>()
    private var mExplContent = 0//0开奖记录、1coco动画、2直播
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //views
        lotCupIv.visible(tGame.cupVisible(mGameType, mGameId))
        lotHistoryRv.layoutManager = LinearLayoutManager(this)
        if (tCocos.hasCocosAnim(mGameType, mGameId)) {
            vm.downloadCocos(tCocos.cocosName(mGameType, mGameId))
        }
        //经典玩法、单式输入法适配
        KeyBoards.fixAndroidBug5497(this)
        KeyBoards.registerSoftInputChangedListener(window,
            object : KeyBoards.OnSoftInputChangedListener {
                override fun onSoftInputChanged(height: Int) {
                    val softInputVisible = KeyBoards.isSoftInputVisible(this@LotActivity)
                    if (softInputVisible && lotExpl.isExpanded) {
                        lotExpl.collapse()
                    }
                }
            })

        //data
        vm.setGameId(mGameId)
        vm.bindService(mGameId)
        vm.getHistoryByGameId(mGameId.toString())
        lotTopLeftAreaLl.setOnClickListener {
            switchExplContent(0)
        }
        lotCupIv.setOnClickListener {
            switchExplContent(1)
        }

        //select 页面
        switchFragment(0)
    }

    private fun switchExplContent(expl2Show: Int) {
        val expanded = lotExpl.isExpanded
        if(!expanded)KeyBoards.hideSoftInput(this)
        if (mExplContent == expl2Show || !expanded) lotExpl.toggle()
        if (mExplContent == expl2Show) return
        lotCocosWv.visibility = if (expl2Show == 1) View.VISIBLE else View.INVISIBLE
        lotHistoryRv.visible(expl2Show == 0)
        mExplContent = expl2Show
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.unBindService(mGameId)
        KeyBoards.unregisterSoftInputChangedListener(window)
    }
    //endregion

    //region 切换fragment
    private var mFragmentIndex = -1// 0经典 1传统 2微投
    private fun switchFragment(index: Int) {
        if (mFragmentIndex == index) return
        //传统显示label
        lotNumsLabelFl.visible(1 == index)
        supportFragmentManager.beginTransaction().let {
            //上一个页面
            if (mFragmentIndex < fragments.size && mFragmentIndex >= 0) {
                val preFragment = fragments[mFragmentIndex]
                if (preFragment.isAdded) {
                    it.hide(preFragment)
                }
            }

            //当前的页面
            if (index < fragments.size && index >= 0) {
                val fragment = fragments[index]
                val tag = tags[index]
                if (fragment.isAdded) {
                    it.show(fragment)
                } else {
                    it.add(R.id.lot_rect_content_fl, fragment, tag)
                }
            }
            it.commitAllowingStateLoss()
        }
        mFragmentIndex = index
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
            lotTopRectIssueTv.text =
                tGame.shortIssueTextWithGameName(it.issueno, mGameName, mGameType)
            val nums = it.nums
            if (!nums.isSpace()) {
                val split = nums!!.split(" ")
                val brightIndexs = tGame.brightIndexs("1", mGameType)
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
    private fun historyIssueNums(historyIssues: List<HistoryData.HistoryItem>?) {
        lotHistoryRv.setListOrUpdate(historyIssues?.toMutableList()) {
            BallAdapter(mGameType, Sizes.dp2px(16f), it?.toMutableList())
        }
    }
    //endregion

    //region 倒计时
    @Inject
    lateinit var tTime: TTime
    private fun coundown(currentTime: CountDownData.CurrentTime?) {
        currentTime?.let {
            val isclose = it.isclose
            val issue = Games.shortIssueText(it.issueno, mGameType)
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

    //region 跑马灯title
    override fun actbarCenter(center: View) {
        val marqueeView = center as MarqueeView<String>
        val playName = "五星直选"
        marqueeView.startWithList(listOf(playName, mGameName))
    }
    //endregion
}