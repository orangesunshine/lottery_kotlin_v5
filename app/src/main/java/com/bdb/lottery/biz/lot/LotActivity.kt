package com.bdb.lottery.biz.lot

import android.Manifest
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.biz.lot.tr.LotTrFragment
import com.bdb.lottery.biz.lot.wt.LotWtFragment
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.cocos.TCocos
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.dialog.lot.LotDialog
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.adapterPattern.OnTabSelectedListenerAdapter
import com.bdb.lottery.utils.game.Games
import com.bdb.lottery.utils.game.TGame
import com.bdb.lottery.utils.thread.TThread
import com.bdb.lottery.utils.time.TTime
import com.bdb.lottery.utils.time.Times
import com.bdb.lottery.utils.ui.activity.Activitys
import com.bdb.lottery.utils.ui.keyboard.KeyBoards
import com.bdb.lottery.utils.ui.size.Sizes
import com.bdb.lottery.utils.webview.TWebView
import com.google.android.material.tabs.TabLayout
import com.sunfusheng.marqueeview.MarqueeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.actionbar_lot_layout.*
import kotlinx.android.synthetic.main.lot_activity.*
import kotlinx.android.synthetic.main.lot_jd_money_unit.*
import permissions.dispatcher.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
@RuntimePermissions
class LotActivity : BaseActivity(R.layout.lot_activity) {
    private val JD_FRAGMENT_TAG: String = "JD_FRAGMENT_TAG"
    private val TR_FRAGMENT_TAG: String = "TR_FRAGMENT_TAG"
    private val WT_FRAGMENT_TAG: String = "WT_FRAGMENT_TAG"

    private val tags: Array<String> = arrayOf(
        JD_FRAGMENT_TAG,
        TR_FRAGMENT_TAG,
        WT_FRAGMENT_TAG
    )

    override var statusbarLight = false;//状态栏是否半透明

    @Inject
    lateinit var tGame: TGame

    @Inject
    lateinit var tLot: TLot
    private val vm by viewModels<LotViewModel>()
    private var mExplContent = 0//0开奖记录、1coco动画、2直播
    private var mPlayMenuVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //views
        initCup()//奖杯
        initCocosWebView()//初始化cocoswebview、并下载cocos动画文件
        lotHistoryRv.layoutManager = LinearLayoutManager(this)
        lotMenuPlayLayer1Rv.layoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        lotMenuPlayLayer2Rv.layoutManager = LinearLayoutManager(this)
        initPlayMenuListener()//点击title，弹出玩法菜单窗口
        playMenu()//玩法菜单
        tLot.switchFragment(0, tags, fragments) { lotNumsLabelFl.visible(1 == it) }//默认选中经典

        //经典玩法、单式输入法适配
        tLot.adjustSoftInput(object : KeyBoards.OnSoftInputChangedListener {
            override fun onSoftInputChanged(height: Int) {
                val softInputVisible = KeyBoards.isSoftInputVisible(this@LotActivity)
                if (softInputVisible && lotExpl.isExpanded) {//软键盘弹出，关闭cocos、历史记录窗口
                    lotExpl.collapse(false)
                }
            }
        })

        lotTopLeftAreaLl.setOnClickListener {
            switchExplContent(0)
        }

        //data
        vm.setGameId(mGameId)
        vm.bindService(mGameId)
        vm.getHistoryByGameId(mGameId.toString())
    }

    override fun observe() {
        vm.countDown.getLiveData().observe(this, { countdown(it) })//倒计时
        vm.curIssue.getLiveData().observe(this, { curIssueNums(it) })//开奖号码
        vm.historyIssue.getLiveData().observe(this, { historyIssueNums(it) })//历史开奖号码
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.unBindService(mGameId)
        KeyBoards.unregisterSoftInputChangedListener(window)
    }

    //region 初始化gameId、gameType、gameName、fragment
    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    private var mPlayName: String? = null
    private var mJdEnable: Boolean = false
    private var mTrEnable: Boolean = false
    private var mWtEnable: Boolean = false
    private var mLotJdFragment: LotJdFragment? = null
    private var mLotTrFragment: LotTrFragment? = null
    private var mLotWtFragment: LotWtFragment? = null
    private var fragments = mutableListOf<Fragment>()
    override fun initVar(bundle: Bundle?) {
        super.initVar(bundle)
        mGameType = intent.getIntExtra(EXTRA.TYPE_GAME_EXTRA, -1)
        mGameId = intent.getIntExtra(EXTRA.ID_GAME_EXTRA, -1)
        mGameName = intent.getStringExtra(EXTRA.NAME_GAME_EXTRA)
        if (-1 == mGameId || -1 == mGameType) {
            finish()
        }
        initFragment(bundle)
    }

    private fun initFragment(bundle: Bundle?) {
        fragments.clear()
        mJdEnable = intent.getBooleanExtra(EXTRA.JD_ENABLE_GAME_EXTRA, false)
        mTrEnable = intent.getBooleanExtra(EXTRA.TR_ENABLE_GAME_EXTRA, false)
        mWtEnable = intent.getBooleanExtra(EXTRA.WT_ENABLE_GAME_EXTRA, false)
        val saveStates = null != bundle
        val fm = supportFragmentManager
        if (mJdEnable) {
            val jd = fm.findFragmentByTag(tags[0])
            mLotJdFragment = if (saveStates && null != jd && jd is LotJdFragment) jd
            else LotJdFragment.newInstance(mGameType, mGameId, mGameName)
            fragments.add(mLotJdFragment!!)
        }

        if (mTrEnable) {
            val tr = fm.findFragmentByTag(tags[1])
            mLotTrFragment = if (saveStates && null != tr && tr is LotTrFragment) tr
            else LotTrFragment.newInstance(mGameType, mGameId, mGameName)
            fragments.add(mLotTrFragment!!)
        }

        if (mWtEnable) {
            val wt = fm.findFragmentByTag(tags[2])
            mLotWtFragment = if (saveStates && null != wt && wt is LotWtFragment) wt
            else LotWtFragment.newInstance(mGameType, mGameId, mGameName)
            fragments.add(mLotWtFragment!!)
        }
    }
    //endregion

    //region 初始化奖杯
    private fun initCup() {
        lotCupIv.visible(tGame.cupVisible(mGameType, mGameId))
        lotCupIv.setOnClickListener {
            switchExplContent(1)
        }
    }
    //endregion

    //region 开奖号码
    private fun curIssueNums(curIssue: HistoryData.HistoryItem?) {
        curIssue?.let {
            mCurIssue = it.issueno
            lotTopRectIssueTv.text =
                tGame.shortIssueTextWithGameName(it.issueno, mGameName, mGameType)
            val nums = it.nums
            if (!nums.isSpace()) {
                //获取开奖号码成功，加载cocos动画
                if (mOpenNumberGeted.compareAndSet(false, true)) {
                    lazyLoadCocos()
                }
                mOpenNums = nums
                lotOpenNumsFl.adapter = tLot.openNumsAdapter(mGameType, nums!!)
            }
        }
    }
    //endregion

    //region 开关cocos、直播窗口
    private fun switchExplContent(expl2Show: Int) {
        val expanded = lotExpl.isExpanded
        if (!expanded) KeyBoards.hideSoftInput(this)
        if (mPlayMenuVisible) {
            gonePlayMenu()
        }
        if (mExplContent == expl2Show || !expanded) lotExpl.toggle()
        if (mExplContent == expl2Show) return
        lotCocosWv.visibility = if (expl2Show == 1) View.VISIBLE else View.INVISIBLE
        lotHistoryRv.visible(expl2Show == 0)
        mExplContent = expl2Show
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
    private fun countdown(currentTime: CountDownData.CurrentTime?) {
        currentTime?.let {
            if (mCountDownGeted.compareAndSet(false, true)) {
                lazyLoadCocos()
            }
            mOpenFormatTime = Times.formatHMS(it.lotteryTime)
            val data = tLot.countDownData(it, mLotJdFragment)
            val showHour = data.size > 2
            lotCountdownDotFirstTv.visible(showHour)
            lotCountdownFourthTv.visible(!showHour)
            val issue = Games.shortIssueText(currentTime.issueno, mGameType)
            lotIssueStatusTv.text = StringBuilder().append(issue).append("期 ")
                .append(if (it.isclose) "封盘中" else "受注中").toString()
            lotCountdownFirstTv.text = if (showHour) data[0] else data[0].let {
                val charArray = it.toCharArray()
                if (charArray.isNotEmpty()) charArray[0].toString() else "0"
            }

            lotCountdownSecondTv.text = if (showHour) data[1] else data[0].let {
                val charArray = it.toCharArray()
                if (charArray.size > 1) charArray[1].toString() else "0"
            }

            lotCountdownThirdTv.text = if (showHour) data[2] else data[1].let {
                val charArray = it.toCharArray()
                if (charArray.isNotEmpty()) charArray[0].toString() else "0"
            }

            if (!showHour)
                lotCountdownFourthTv.text = data[1].let {
                    val charArray = it.toCharArray()
                    if (charArray.size > 1) charArray[1].toString() else "0"
                }

            if (lotDialog.isAdded) {
                lotDialog.countdown(it)
            }
        }
    }
    //endregion

    //region 跑马灯title
    private lateinit var mMarqueeView: MarqueeView<String>
    override fun actbarCenter(center: View) {
        mMarqueeView = center as MarqueeView<String>
        updateMarqueeView("")
    }

    fun updateMarqueeView(playName: String?) {
        mPlayName = playName;
        mMarqueeView.startWithList(
            if (playName.isSpace()) listOf(mGameName, playName)
            else listOf(playName, mGameName)
        )
    }

    override fun onStart() {
        super.onStart()
        mMarqueeView?.startFlipping()
    }

    override fun onStop() {
        super.onStop()
        mMarqueeView?.stopFlipping()
    }
    //endregion

    //region sd卡权限，cocos下载
    @Inject
    lateinit var tCocos: TCocos

    @Inject
    lateinit var tThread: TThread

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun cocosDownload() {
        //下载全部cocos文件
        val name = tCocos.cocosName(mGameType, mGameId)
        vm.downloadCocos(name) {
            //cocos初始化完成监听
            tThread.runOnUiThread {
                tCocos.initCocosEngine(it, name, lotCocosWv) {
                    if (mCocosEngineInited.compareAndSet(false, true)) {
                        lazyLoadCocos()
                    }
                }
            }
        }
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    //给用户解释要请求什么权限，为什么需要此权限
    fun showSingleRationale(request: PermissionRequest) {
        val activity = mActivity?.get()
        if (Activitys.isActivityAlive(activity)) {
            AlertDialog.Builder(activity!!)
                .setMessage("拒绝将影响应用的正常使用，请允许")
                .setPositiveButton("允许") { dialog, which ->
                    request.proceed() //继续执行请求
                }.setNegativeButton("拒绝") { dialog, which ->
                    request.cancel() //取消执行请求
                }.show()
        }
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE) //一旦用户拒绝了
    fun storageDenied() {
        toast.showWarning("SD卡权限已拒绝")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
    //endregion


    //region 倒计时接口、开奖号码数据获取成功、cocos引擎初始化完成后调用cocos的js方法启动cocos动画
    @Inject
    lateinit var tWebView: TWebView
    private var mCocosEngineInited = AtomicBoolean(false)//cocos引擎初始化
    private var mCountDownGeted = AtomicBoolean(false)//倒计时获取
    private var mOpenNumberGeted = AtomicBoolean(false)//开奖号码获取

    private fun initCocosWebView() {
        tWebView.initConfig(lotCocosWv)
        if (tCocos.hasCocosAnim(mGameType, mGameId)) {
            cocosDownloadWithPermissionCheck()
        }
    }

    private var mCurIssue: String? = null//当前期号
    private var mOpenNums: String? = null//开奖号码
    private var mOpenFormatTime: String? = null//开奖时分秒格式化时间

    private fun lazyLoadCocos() {
        if (mCocosEngineInited.get() && mCountDownGeted.get() && mOpenNumberGeted.get()) {
            tThread.runOnUiThread {
                //调用cocos的js方法，传递开奖号码、期号、开奖时间、名称
                tCocos.cocosLoadData(
                    lotCocosWv, mOpenNums,
                    mCurIssue,
                    mOpenFormatTime,
                    tCocos.cocosName(mGameType, mGameId)
                )
            }
        }
    }
    //endregion


    //region 玩法菜单：fragment切换tab、玩法列表
    private fun playMenu() {
        val tabs = arrayOf("经典", "传统", "微投")
        tabs.forEach {
            val tab = lotMenuSelectFragmentTl.newTab()
            val tabView = TextView(this)
            tabView.textSize = 14F
            tabView.text = it
            tabView.gravity = Gravity.CENTER_HORIZONTAL
            tab.customView = tabView
            lotMenuSelectFragmentTl.addTab(tab)
        }
        lotMenuSelectFragmentTl.addOnTabSelectedListener(object : OnTabSelectedListenerAdapter() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab.updateTab(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab.updateTab(false)
            }
        })
        lotMenuSelectFragmentTl.getTabAt(0).updateTab(true)//选中第一个
    }
    //endregion

    //region 点击title，选择玩法
    private fun initPlayMenuListener() {
        actionbar_center_id.setOnItemClickListener { _: Int, _: TextView ->
            mPlayMenuVisible = !mPlayMenuVisible
            if (mPlayMenuVisible) {
                //弹出玩法菜单：关闭cocos、历史记录窗口，软键盘关闭
                if (lotExpl.isExpanded) {
                    lotExpl.collapse(false)
                }
                KeyBoards.hideSoftInput(this)
            }
            lotMenuLl.visible(mPlayMenuVisible)
        }
        lotMenuLl.setOnClickListener {
            gonePlayMenu()
        }
    }

    fun gonePlayMenu() {
        mPlayMenuVisible = false
        lotMenuLl.visible(false)
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

    //region 下注弹窗
    @Inject
    lateinit var lotDialog: LotDialog
    fun lotByDialog(
        token: String?,
        nums: String?,
        multiply: String,
        unit: Int,
        success: (() -> Unit)? = null,
        error: ((String) -> Unit)? = null,
    ) {
        lotDialog.gameId(mGameId).lotName(mGameName).lotPlayName(mPlayName).lotNums(nums)
            .lotMultiply(multiply).lotAmount(2.0, unit)
        lotDialog.show(supportFragmentManager)
    }
    //endregion
}
