package com.bdb.lottery.biz.lot.activity

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.biz.lot.BallAdapter
import com.bdb.lottery.biz.lot.TLot
import com.bdb.lottery.biz.lot.dialog.lot.LotDialog
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.biz.lot.jd.single.LotBetAdapter
import com.bdb.lottery.biz.lot.jd.single.LotPlayAdapter
import com.bdb.lottery.biz.lot.tr.LotTrFragment
import com.bdb.lottery.biz.lot.wt.LotWtFragment
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.cocos.TCocos
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayGroupItem
import com.bdb.lottery.datasource.lot.data.jd.PlayItem
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.adapterPattern.OnTabSelectedListenerAdapter
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.game.TGame
import com.bdb.lottery.utils.thread.TThread
import com.bdb.lottery.utils.time.TTime
import com.bdb.lottery.utils.time.Times
import com.bdb.lottery.utils.ui.activity.Activitys
import com.bdb.lottery.utils.ui.keyboard.KeyBoards
import com.bdb.lottery.utils.ui.popup.CommonPopData
import com.bdb.lottery.utils.ui.popup.CommonPopWindow
import com.bdb.lottery.utils.ui.size.Sizes
import com.bdb.lottery.utils.webview.TWebView
import com.bdb.lottery.widget.LoadingLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.tabs.TabLayout
import com.sunfusheng.marqueeview.MarqueeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.actionbar_lot_layout.*
import kotlinx.android.synthetic.main.lot_activity.*
import permissions.dispatcher.*
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
@RuntimePermissions
class LotActivity : BaseActivity(R.layout.lot_activity) {
    @Inject
    lateinit var tGame: TGame

    @Inject
    lateinit var tLot: TLot

    override var statusbarLight = false;//状态栏是否半透明

    private val vm by viewModels<LotViewModel>()
    lateinit var mPlayLoadingLayout: LoadingLayout

    override fun initVar(bundle: Bundle?) {
        super.initVar(bundle)
        //获取初始化彩种大类、彩种id失败，直接finish
        if (!vm.initExtraArgs(intent)) {
            toast.showError("配置错误！")
            finish()
        }
        playSelectedPosCache()

        initFragment(bundle)
    }

    private var timeRecord: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        timeRecord = SystemClock.elapsedRealtime()
        super.onCreate(savedInstanceState)
        initCup()//奖杯
        skin4GameType()//换肤

        //形态文本框宽度（时时彩70dp，其他40dp）
        lotJdHistoryLabelTv.layoutParams.width =
            Sizes.dp2px(if (vm.isSSC()) 70f else 40f)
        lotHistoryRv.layoutManager = LinearLayoutManager(this)//开奖历史列表控件初始化

        initCocosWebView()//初始化cocoswebview、并下载cocos动画文件

        mPlayLoadingLayout = LoadingLayout.wrap(lotMenuLl)//玩法获取失败组件
        //玩法二级菜单控件初始化
        lotMenuPlayLayer1Rv.layoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        lotMenuPlayLayer2Rv.layoutManager = LinearLayoutManager(this)
        playMenu()//玩法菜单

        adjustSoftInput()//经典玩法、单式输入法适配

        titleClick()//点击：标题栏title
        playMenuBgClick()//点击：玩法列表背景
        openHistoryClick()//点击：开奖历史
        actbarMenuClick()//点击：标题栏菜单

        switchFragment(0)//默认选中经典

        //data
        requestDatas()
        Timber.d("onCreate--period: " + (SystemClock.elapsedRealtime() - timeRecord))
    }

    //region 玩法缓存：读取上次彩种选中玩法
    @Inject
    lateinit var tCache: TCache
    private var mPlaySelectedPos: Int = 0
    private var mGroupSelectedPos: Int = 0
    private var mBetSelectedPos: Int = 0
    private var mBetTypeId: Int = 0
    private var mParentPlayId: Int = 0
    private fun playSelectedPosCache() {
        val gameId = vm.getGameId()
        mPlaySelectedPos = tCache.playCacheByGameId(gameId) ?: 0
        mGroupSelectedPos = tCache.playGroupCacheByGameId(gameId) ?: 0
        mBetSelectedPos = tCache.betCacheByGameId(gameId) ?: 0
        mParentPlayId = tCache.parentPlayIdCacheByGameId(gameId) ?: 0
        mBetTypeId = tCache.playIdCacheByGameId(gameId) ?: 0
    }
    //endregion

    //region 玩法缓存：选中玩法后缓存（二级玩法选中）
    private fun cachePlay4GameId(gameId: Int) {
        tCache.cachePlay4GameId(
            gameId, mPlaySelectedPos, mGroupSelectedPos, mBetSelectedPos, mParentPlayId, mBetTypeId
        )
    }
    //endregion

    //region 网络数据：倒计时、开奖历史、余额
    private fun requestDatas() {
        vm.bindService(this)
        vm.getHistoryByGameId()
        vm.refreshBalanceByPostGlobalEvent()
    }
    //endregion

    //region 软键盘适配--单式
    private fun adjustSoftInput() {
        tLot.adjustSoftInput(object : KeyBoards.OnSoftInputChangedListener {
            override fun onSoftInputChanged(height: Int) {
                val softInputVisible = KeyBoards.isSoftInputVisible(this@LotActivity)
                if (softInputVisible && lotOpenHistoryCocosExpl.isExpanded) {//软键盘弹出，关闭cocos、历史记录窗口
                    lotOpenHistoryCocosExpl.collapse(false)
                }
            }
        })
    }
    //endregion

    //region 点击：标题栏菜单
    private fun actbarMenuClick() {
        actionbar_right_id.setOnClickListener {
            popActbarRightMenu()
        }
    }
    //endregion

    //region 点击：开奖历史
    private fun openHistoryClick() {
        lotTopLeftAreaLl.setOnClickListener {
            switchExplContent(0)
        }
    }
    //endregion

    //region 切换fragment
    private var mTabIndex = -1//tab下标
    private fun switchFragment(index: Int) {
        switchFragment(index, tags, fragments) {
            //kg显示label
            lotNumsLabelFl.visible(1 == it)
        }
    }

    private fun switchFragment(
        index: Int,
        tags: List<String>,
        fragments: List<Fragment>,
        afterSwitch: ((Int) -> Unit)? = null,
    ) {
        if (mTabIndex == index) return
        //传统显示label
        supportFragmentManager.beginTransaction().let {
            //上一个页面
            if (mTabIndex < fragments.size && mTabIndex >= 0) {
                val preFragment = fragments[mTabIndex]
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
        afterSwitch?.invoke(index)
        mTabIndex = index
        getCurFragmentType(index)
    }

    private val JD_FRAGMENT_TYPE = 0//经典
    private val TR_FRAGMENT_TYPE = 1//传统
    private val WT_FRAGMENT_TYPE = 2//微投
    private var mCurFragmentType = JD_FRAGMENT_TYPE
    private fun getCurFragmentType(tabIndex: Int) {
        mCurFragmentType = when (tabIndex) {
            0 -> if (!mJdEnable) TR_FRAGMENT_TYPE else if (!mJdEnable && !mTrEnable) WT_FRAGMENT_TYPE else JD_FRAGMENT_TYPE
            1 -> if (!mJdEnable || !mTrEnable) WT_FRAGMENT_TYPE else TR_FRAGMENT_TYPE
            2 -> WT_FRAGMENT_TYPE
            else -> JD_FRAGMENT_TYPE
        }
    }
    //endregion

    override fun observe() {
        vm.countDown.getLiveData().observe(this, { countdown(it) })//倒计时
        vm.curIssue.getLiveData().observe(this, { curIssueNums(it) })//开奖号码
        vm.historyIssue.getLiveData().observe(this, { historyIssueNums(it) })//历史开奖号码
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.unBindService(this)
        KeyBoards.unregisterSoftInputChangedListener(window)
    }

    //region 初始化gameId、gameType、gameName、fragment
    private var mPlayName: String? = null//五星·直选复式
    private var mJdEnable: Boolean = false
    private var mTrEnable: Boolean = false
    private var mWtEnable: Boolean = false
    private var mLotJdFragment: LotJdFragment? = null
    private var mLotTrFragment: LotTrFragment? = null
    private var mLotWtFragment: LotWtFragment? = null
    private var fragments = mutableListOf<Fragment>()
    private val tags = mutableListOf<String>()
    private val JD_FRAGMENT_TAG: String = "JD_FRAGMENT_TAG"
    private val TR_FRAGMENT_TAG: String = "TR_FRAGMENT_TAG"
    private val WT_FRAGMENT_TAG: String = "WT_FRAGMENT_TAG"
    private fun initFragment(bundle: Bundle?) {
        tags.clear()
        fragments.clear()
        mJdEnable = intent.getBooleanExtra(EXTRA.JD_ENABLE_GAME_EXTRA, false)
        mTrEnable = intent.getBooleanExtra(EXTRA.TR_ENABLE_GAME_EXTRA, false)
        mWtEnable = intent.getBooleanExtra(EXTRA.WT_ENABLE_GAME_EXTRA, false)
        val saveStates = null != bundle
        val fm = supportFragmentManager
        if (mJdEnable) {
            tags.add(JD_FRAGMENT_TAG)
            val jd = fm.findFragmentByTag(JD_FRAGMENT_TAG)
            mLotJdFragment =
                if (saveStates && null != jd && jd is LotJdFragment) jd else vm.createJdFragment(
                    mBetTypeId
                )
            fragments.add(mLotJdFragment!!)
        }

        if (mTrEnable) {
            tags.add(TR_FRAGMENT_TAG)
            val tr = fm.findFragmentByTag(TR_FRAGMENT_TAG)
            mLotTrFragment =
                if (saveStates && null != tr && tr is LotTrFragment) tr else vm.createTrFragment()
            fragments.add(mLotTrFragment!!)
        }

        if (mWtEnable) {
            tags.add(WT_FRAGMENT_TAG)
            val wt = fm.findFragmentByTag(WT_FRAGMENT_TAG)
            mLotWtFragment =
                if (saveStates && null != wt && wt is LotWtFragment) wt else vm.createWtFragment()
            fragments.add(mLotWtFragment!!)
        }
    }
    //endregion

    //region 换肤：标题栏、倒计时栏（背景、开奖历史、文字颜色、分割线）
    private fun skin4GameType() {
        //title
        if (mCurFragmentType == WT_FRAGMENT_TYPE) {
            lotTitleLl.setBackgroundColor(Color.parseColor("#26262A"))
        } else {
            lotTitleLl.setBackgroundResource(vm.titleSkinSkin())
        }

        //倒计时栏
        val textColor: Int = vm.openHistoryTextColor()
        lotTopRectIssueTv.setTextColor(textColor)//历史期号
        lotTopRectHistoryOpenTv.setTextColor(textColor)//往期开奖
        lotIssueStatusTv.setTextColor(textColor)//当前期号、受注状态
        lotTopRectHistoryOpenLl.setBackgroundResource(vm.openHistoryBtnSkin())//往期开奖背景
        lotTopRectHistoryOpenIv.setImageResource(vm.openHistoryBtnArrowSkin())//往期开奖右侧箭头
        lotAreaTopDivide.visible(vm.titleBottomDivideVisible())//title和倒计时栏之间分割线
        //top区域左右分割线
        countDownDivide.layoutParams.width =
            Sizes.dp2px(if (vm.isPK()) 3f else 1f)
        countDownDivide.setBackgroundResource(vm.countDownDivideSkin())
        //top区域背景
        countDownBannerLl.setBackgroundResource(
            vm.countDownBannerSkin()
        )

        //开奖历史
        val k3 = vm.isK3()
        if (k3) {
            val k3TextColor = ContextCompat.getColor(
                this,
                R.color.color_skin_k3_text_color
            )
            lotJdHistoryLl.setBackgroundResource(R.color.color_skin_k3_bg)
            lotJdHistoryIssueTv.setTextColor(k3TextColor)
            lotJdHistoryOpenTv.setTextColor(k3TextColor)
            lotJdHistoryLabelTv.setTextColor(k3TextColor)
            lotHistoryLine.setBackgroundResource(R.color.color_skin_k3_line)
        }
        lotOpenNumsDivideView.visible(k3)//开奖历史-分割线
        lotJdHistoryLabelTv.visible(!k3)//开奖历史-形态

        //倒计时-文字
        val countDownSkinTextColor = ContextCompat.getColor(this, vm.countDownTextColor())
        lotCountdownFirstTv.setTextColor(countDownSkinTextColor)
        lotCountdownDotFirstTv.setTextColor(countDownSkinTextColor)
        lotCountdownSecondTv.setTextColor(countDownSkinTextColor)
        lotCountdownDotSecondTv.setTextColor(countDownSkinTextColor)
        lotCountdownThirdTv.setTextColor(countDownSkinTextColor)
        lotCountdownFourthTv.setTextColor(countDownSkinTextColor)

        //倒计时-背景
        val countDownSkinBg = vm.countDownSkin(WT_FRAGMENT_TYPE == mCurFragmentType)
        lotCountdownFirstTv.setBackgroundResource(countDownSkinBg)
        lotCountdownSecondTv.setBackgroundResource(countDownSkinBg)
        lotCountdownThirdTv.setBackgroundResource(countDownSkinBg)
        lotCountdownFourthTv.setBackgroundResource(countDownSkinBg)
    }
    //endregion

    //region 初始化奖杯
    private fun initCup() {
        lotCupIv.visible(vm.cupVisible())
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
                vm.shortIssueTextWithGameName(it.issueno)
            val nums = it.nums
            if (!nums.isSpace()) {
                //获取开奖号码成功，加载cocos动画
                if (mOpenNumberGeted.compareAndSet(false, true)) {
                    lazyLoadCocos()
                }
                mOpenNums = nums
                lotOpenNumsFl.adapter =
                    vm.createOpenBallAdapter(this@LotActivity, nums!!, mPlayName)
            }
        }
    }
    //endregion

    //region 开关cocos、直播窗口
    private var mExplContent = 0//0开奖记录、1coco动画、2直播
    private fun switchExplContent(expl2Show: Int) {
        val expanded = lotOpenHistoryCocosExpl.isExpanded
        if (!expanded) KeyBoards.hideSoftInput(this)
        if (mPlayMenuVisible) {
            gonePlayMenu()
        }
        if (mExplContent == expl2Show || !expanded) {
            //arrow动画
            lotTopRectHistoryOpenIv.clearAnimation()
            lotTopRectHistoryOpenIv.startAnimation(RotateAnimation(
                if (expanded) 180f else 0f,
                if (expanded) 0f else 180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                fillAfter = true
                duration = 300
            })
            lotOpenHistoryCocosExpl.toggle()
        }
        if (mExplContent == expl2Show) return
        lotCocosWv.visibility = if (expl2Show == 1) View.VISIBLE else View.INVISIBLE
        lotHistoryRv.visible(expl2Show == 0)
        mExplContent = expl2Show
    }
    //endregion

    //region 历史开奖号码
    private fun historyIssueNums(historyIssues: List<HistoryData.HistoryItem>?) {
        lotHistoryRv.setListOrUpdate(historyIssues?.toMutableList()) {
            vm.createBallAdapter(
                mParentPlayId,
                mBetTypeId,
                mPlayName,
                it?.toMutableList()
            )
        }
    }

    fun updateHistoryLabel(parentPlayId: Int) {
        mParentPlayId = parentPlayId
        lotHistoryRv.adapter?.let {
            if (it is BallAdapter) {
                it.onBetChange(
                    parentPlayId,
                    vm.lotPlace(mPlayName)
                )
            }
        }
    }
    //endregion

    //region 倒计时
    @Inject
    lateinit var tTime: TTime
    private var mIsClose = false
    private fun countdown(currentTime: CountDownData.CurrentTime?) {
        currentTime?.let {
            mCurIssue = it.issueno
            mIsClose = it.isclose
            if (mCountDownGeted.compareAndSet(false, true)) {
                lazyLoadCocos()
            }
            mOpenFormatTime = Times.formatHMS(it.lotteryTime)
            val data = tLot.countDownData(it, mLotJdFragment)
            val showHour = data.size > 2
            lotCountdownDotFirstTv.visible(showHour)
            lotCountdownFourthTv.visible(!showHour)
            val issue = vm.shortIssueText(it.issueno)
            lotIssueStatusTv.text = StringBuilder().append(issue).append("期 ")
                .append(if (mIsClose) "封盘中" else "受注中").toString()
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
        vm.marqueeTextList("")
    }

    fun updateMarqueeView(playName: String?) {
        mPlayName = playName
        mMarqueeView.startWithList(vm.marqueeTextList(playName))
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onCreate--period: " + (SystemClock.elapsedRealtime() - timeRecord))
        mMarqueeView.startFlipping()
    }

    override fun onStop() {
        super.onStop()
        mMarqueeView.stopFlipping()
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
        val name = vm.cocosName()
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
                .setPositiveButton("允许") { _, _ ->
                    request.proceed() //继续执行请求
                }.setNegativeButton("拒绝") { _, _ ->
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
        if (vm.hasCocosAnim()) {
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
                    vm.cocosName()
                )
            }
        }
    }
    //endregion


    //region 玩法菜单：fragment切换tab、玩法列表
    private var mPlayMenuVisible = false
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
                val position = tab?.position
                position?.let {
                    switchFragment(it)
                }
                mPlayMenuVisible = false
                lotMenuLl.visible(mPlayMenuVisible)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab.updateTab(false)
            }
        })
        lotMenuSelectFragmentTl.getTabAt(0).updateTab(true)//选中第一个
    }
    //endregion

    //region 点击：玩法列表背景
    private fun playMenuBgClick() {
        lotMenuLl.setOnClickListener {
            gonePlayMenu()
        }
    }
    //endregion

    //region 点击：标题栏title
    private fun titleClick() {
        actionbar_center_id.setOnItemClickListener { _: Int, _: TextView ->
            mPlayMenuVisible = !mPlayMenuVisible
            actionbar_center_id.clearAnimation()
            lot_actionbar_center_arrow_iv.startAnimation(
                RotateAnimation(
                    if (mPlayMenuVisible) 0f else 180f,
                    if (mPlayMenuVisible) 180f else 0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    fillAfter = true
                    duration = 300
                }
            )
            if (mPlayMenuVisible) {
                //弹出玩法菜单：关闭cocos、历史记录窗口，软键盘关闭
                if (lotOpenHistoryCocosExpl.isExpanded) {
                    lotOpenHistoryCocosExpl.collapse(false)
                }
                KeyBoards.hideSoftInput(this)
            }
            lotMenuLl.visible(mPlayMenuVisible)
            val isJd = mCurFragmentType == JD_FRAGMENT_TYPE
            lotMenuPlayLayer1Rv.visible(isJd)
            lotMenuPlayLayer2Rv.visible(isJd)
        }
    }
    //endregion

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
        lotParam: LotParam,
        danTiaoTips: String? = null,
        success: ((String) -> Unit)? = null,
        error: ((String) -> Unit)? = null,
    ) {
        //验证倒计时相关：是否获取倒计时、彩种是否休市、彩种是否封盘
        if (!mCountDownGeted.get()) {
            vm.bindService(this)
            toast.showWarning("正在获取期号数据，请稍后再试")
            return
        }

        if (mIsClose) {
            toast.showWarning("当前彩种已封盘")
            return
        }
        lotParam.issueNo = mCurIssue//当前期号
        lotDialog.lotParams(lotParam).lotSuccess(success).lotError(error)
            .setDanTiaoTips(danTiaoTips).show(supportFragmentManager)
    }
    //endregion

    //region 一级玩法
    fun updatePlayList(betTypeDatas: GameBetTypeData?) {
        onBetSelected(
            vm.play2BetByPos(
                mPlaySelectedPos,
                mGroupSelectedPos,
                mBetSelectedPos,
                betTypeDatas
            )
        )
        lotMenuPlayLayer1Rv.setListOrUpdate(betTypeDatas) {
            LotPlayAdapter(betTypeDatas).apply {
                setOnItemClickListener { _: BaseQuickAdapter<*, *>, _: View, position: Int ->
                    //一级玩法选中
                    notifySelectedPositionWithPayLoads(position)
                    updateBetList(betTypeDatas?.get(position), position)
                }
            }
        }
        lotMenuPlayLayer1Rv?.adapter?.let {
            if (it is BaseSelectedQuickAdapter<*, *>) it.notifySelectedPosition(mPlaySelectedPos)
        }
        updateBetList(
            if (betTypeDatas.validIndex(mPlaySelectedPos)) betTypeDatas?.get(mPlaySelectedPos) else null,
            mPlaySelectedPos
        )
    }
    //endregion

    //region 二级玩法、二级玩法组
    private var mPlaySelectedRef = -1
    private fun onBetSelected(item: BetItem?) {
        item?.let {
            if (mCurFragmentType == JD_FRAGMENT_TYPE) {
                mBetTypeId = it.betType
                mLotJdFragment?.onBetSelected(item)
            }
        }
        lotOpenNumsFl.adapter?.let {
            if (it is OpenBallAdapter) {
                it.onBetChange(vm.lotPlace(mPlayName))
            }
        }
    }

    private fun updateBetList(playItem: PlayItem?, playSelectedPos: Int) {
        mPlaySelectedRef = playSelectedPos
        lotMenuPlayLayer2Rv.setListOrUpdate(playItem?.list) {
            object : BaseSelectedQuickAdapter<PlayGroupItem, BaseViewHolder>(
                R.layout.lot_jd_play_group_item, it?.toMutableList()
            ) {
                override fun convert(groupHolder: BaseViewHolder, item: PlayGroupItem) {
                    val groupPosition = groupHolder.adapterPosition
                    val name = item.name
                    groupHolder.setText(R.id.lot_jd_play_group_name_tv, "$name：")
                    groupHolder.getView<RecyclerView>(R.id.lot_jd_play_group_rv).run {
                        layoutManager =
                            GridLayoutManager(
                                context,
                                if (name.length > 3) 2 else 3,
                                RecyclerView.VERTICAL,
                                false
                            )
                        setListOrUpdate(item.list) {
                            object : LotBetAdapter(it) {
                                override fun convert(
                                    holder: BaseViewHolder,
                                    item: BetItem
                                ) {
                                    super.convert(holder, item)
                                    holder.getView<TextView>(R.id.text_common_tv).isSelected =
                                        mPlaySelectedRef == mPlaySelectedPos && groupPosition == mGroupSelectedPos && isItemSelected(
                                            holder
                                        )
                                }

                                override fun convert(
                                    holder: BaseViewHolder,
                                    item: BetItem,
                                    payloads: List<Any>,
                                ) {
                                    holder.getView<TextView>(R.id.text_common_tv).isSelected =
                                        mPlaySelectedRef == mPlaySelectedPos && groupPosition == mGroupSelectedPos && isItemSelected(
                                            holder
                                        )
                                }
                            }.apply {
                                setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View, position: Int ->
                                    //选中玩法：更新一级玩法下标、二级玩法组下标、二级玩法下标
                                    if (mPlaySelectedRef == mPlaySelectedPos && groupPosition == mGroupSelectedPos && mBetSelectedPos == position) return@setOnItemClickListener
                                    if (groupPosition != mGroupSelectedPos) lotMenuPlayLayer2Rv?.adapter?.notifyItemChanged(
                                        groupPosition, PAY_LOADS_SELECTED
                                    )
                                    notifySelectedPositionWithPayLoads(position, false)
                                    cachePlay4GameId(vm.getGameId())//缓存玩法
                                    adapter.getItemOrNull(position)?.let {
                                        if (it is BetItem) {
                                            onBetSelected(it)
                                        }
                                    }
                                    gonePlayMenu()
                                    mBetSelectedPos = position
                                    mGroupSelectedPos = groupPosition
                                    mPlaySelectedPos = mPlaySelectedRef

                                }
                            }
                        }
                    }

                    if (groupPosition == mGroupSelectedPos) {
                        groupHolder.getView<RecyclerView>(R.id.lot_jd_play_group_rv).adapter?.let {
                            if (it is BaseSelectedQuickAdapter<*, *>) {
                                it.notifySelectedPosition(mBetSelectedPos)
                            }
                        }
                    }
                }

                override fun convert(
                    holder: BaseViewHolder,
                    item: PlayGroupItem,
                    payloads: List<Any>,
                ) {
                    holder.getView<RecyclerView>(R.id.lot_jd_play_group_rv).adapter?.let {
                        if (it is BaseSelectedQuickAdapter<*, *>) {
                            it.notifyUnSelectedAllWithPayLoads()
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region 弹出右侧菜单：快捷充值、投注记录、追号记录、走势图、盈亏报表、官方验证
    @Inject
    lateinit var mMenuPopWin: CommonPopWindow
    private fun popActbarRightMenu() {
        val popDatas = ArrayList<CommonPopData>()
        popDatas.add(CommonPopData("快捷充值", {}))
        popDatas.add(CommonPopData("投注记录", {}))
        popDatas.add(CommonPopData("追号记录", {}))
        popDatas.add(CommonPopData("走势图", {}))
        popDatas.add(CommonPopData("盈亏报表", {}))
        popDatas.add(CommonPopData("官方验证", {}))
        mMenuPopWin.init(popDatas, Sizes.dp2px(100f))
        mMenuPopWin.showAtScreenLocation(actionbar_right_id, Sizes.dp2px(8f), -Sizes.dp2px(8f))
    }
    //endregion
}
