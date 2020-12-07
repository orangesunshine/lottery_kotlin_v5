package com.bdb.lottery.biz.lot

import android.Manifest
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.biz.lot.tr.LotTrFragment
import com.bdb.lottery.biz.lot.wt.LotWtFragment
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.cocos.TCocos
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.TouZhuHaoMa
import com.bdb.lottery.datasource.lot.data.ZhuiHaoQiHao
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayGroupItem
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer1Item
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer2Item
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.adapterPattern.OnTabSelectedListenerAdapter
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.game.Games
import com.bdb.lottery.utils.game.TGame
import com.bdb.lottery.utils.thread.TThread
import com.bdb.lottery.utils.time.TTime
import com.bdb.lottery.utils.time.Times
import com.bdb.lottery.utils.ui.activity.Activitys
import com.bdb.lottery.utils.ui.keyboard.KeyBoards
import com.bdb.lottery.utils.ui.size.Sizes
import com.bdb.lottery.utils.webview.TWebView
import com.bdb.lottery.utils.webview.WebViews
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.tabs.TabLayout
import com.sunfusheng.marqueeview.MarqueeView
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.actionbar_lot_layout.*
import kotlinx.android.synthetic.main.lot_activity.*
import permissions.dispatcher.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
@RuntimePermissions
class LotActivity : BaseActivity(R.layout.lot_activity) {
    private val JD_FRAGMENT_TAG: String = "JD_FRAGMENT_TAG"
    private val TR_FRAGMENT_TAG: String = "TR_FRAGMENT_TAG"
    private val WT_FRAGMENT_TAG: String = "WT_FRAGMENT_TAG"

    override var statusbarLight = false;//状态栏是否半透明

    @Inject
    lateinit var tGame: TGame
    private val vm by viewModels<LotViewModel>()
    private var mExplContent = 0//0开奖记录、1coco动画、2直播
    private var mShowMenu = false
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
        switchFragment(0)//select 页面

        //经典玩法、单式输入法适配
        adjustSoftInput()

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

    @Inject
    lateinit var tCache: TCache
    override fun onDestroy() {
        super.onDestroy()
        vm.unBindService(mGameId)
        KeyBoards.unregisterSoftInputChangedListener(window)
        tCache.cachePlay4GameId(mGameId, mPlayLayer1, mPlayGroup, mPlayLayer2, mPlayId)
    }

    //region 经典：单式输入法适配
    private fun adjustSoftInput() {
        KeyBoards.fixAndroidBug5497(this)
        KeyBoards.registerSoftInputChangedListener(window,
            object : KeyBoards.OnSoftInputChangedListener {
                override fun onSoftInputChanged(height: Int) {
                    val softInputVisible = KeyBoards.isSoftInputVisible(this@LotActivity)
                    if (softInputVisible && lotExpl.isExpanded) {//软键盘弹出，关闭cocos、历史记录窗口
                        lotExpl.collapse(false)
                    }
                }
            })
    }
    //endregion

    //region 初始化gameId、gameType、gameName、fragment
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
        playParamsCache(mGameId)

        fragments = if (null == bundle) {
            arrayOf(
                LotJdFragment.newInstance(mGameType, mGameId, mGameName),
                LotTrFragment.newInstance(mGameType, mGameId, mGameName),
                LotWtFragment.newInstance(mGameType, mGameId, mGameName)
            )
        } else {
            arrayOf(
                supportFragmentManager.findFragmentByTag(tags[0]) ?: LotJdFragment.newInstance(
                    mGameType,
                    mGameId,
                    mGameName
                ),
                supportFragmentManager.findFragmentByTag(tags[1]) ?: LotTrFragment.newInstance(
                    mGameType,
                    mGameId,
                    mGameName
                ),
                supportFragmentManager.findFragmentByTag(tags[2]) ?: LotWtFragment.newInstance(
                    mGameType,
                    mGameId,
                    mGameName
                )
            )
        }
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

    //region 开关cocos、直播窗口
    private fun switchExplContent(expl2Show: Int) {
        val expanded = lotExpl.isExpanded
        if (!expanded) KeyBoards.hideSoftInput(this)
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
            val isClosed = it.isclose
            val issue = Games.shortIssueText(it.issueno, mGameType)
            val status = StringBuilder().append(issue).append("期 ")
                .append(if (isClosed) "封盘中" else "受注中")
            lotIssueStatusTv.text = status
            val showHour = it.betTotalTime / 1000 / 60 / 60 > 0
            val surplusTime = tTime.surplusTime2String(
                showHour,
                if (isClosed) it.openSurplusTime else it.betSurplusTime
            )
            showCountDown(surplusTime, showHour)

            if (mFragmentIndex == 0 && !fragments.isEmpty()) {
                (fragments[0] as LotJdFragment).updateStatus(isClosed)
            }
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
        val showHourReal = showHour || (split.size > 2)
        lotCountdownDotFirstTv.visible(showHourReal)
        lotCountdownFourthTv.visible(!showHourReal)
        val hour = if (split.size > 2) split[0] else "00"
        val minite = if (split.size > 2) split[1] else if (split.size > 1) split[0] else "00"
        val second =
            if (split.size > 2) split[2] else if (split.size > 1) split[1] else if (split.isNotEmpty()) split[0] else "00"
        if (showHourReal) {
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
    private var mMarqueeView: MarqueeView<String>? = null
    override fun actbarCenter(center: View) {
        mMarqueeView = center as MarqueeView<String>
        val playName = "五星直选"
        mMarqueeView?.startWithList(listOf(playName, mGameName))
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

    //region 弹窗并下注
    fun lotByDialog(
        token: String,
        touZhuHaoMa: List<TouZhuHaoMa>?,
        zhuiHaoQiHao: List<ZhuiHaoQiHao>?,
        error: (token: String) -> Unit,
    ) {
        vm.lot(
            LotParam(
                mGameId.toString(),
                mGameName!!,
                mCurIssue!!,
                mFragmentIndex > 0,
                false,
                token,
                touZhuHaoMa,
                zhuiHaoQiHao
            ), {}, error
        )
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
            //加载cocos动画
            tThread.runOnUiThread {
                //加载cocos文件
                lotCocosWv.loadUrl(tCocos.cocosLoadUrl(it, name))
                val cocosH5: TCocos.Cocos2H5 = object : TCocos.Cocos2H5() {
                    @JavascriptInterface
                    override fun onLoadDataCallback() {
                        if (mCocosEngineInited.compareAndSet(false, true)) {
                            lazyLoadCocos()
                        }
                    }
                }
                lotCocosWv.addJavascriptInterface(cocosH5, "AndroidMessage")
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

    //调用cocos的js方法，传递开奖号码、期号、开奖时间、名称
    fun lazyLoadCocos() {
        if (mCocosEngineInited.get() && mCountDownGeted.get() && mOpenNumberGeted.get()) {
            tThread.runOnUiThread {
                WebViews.loadUrl(
                    lotCocosWv, "callFromJava",
                    tCocos.genLoadCocosParams(
                        mOpenNums,
                        mCurIssue,
                        mOpenFormatTime,
                        tCocos.cocosName(mGameType, mGameId)
                    )
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
            mShowMenu = !mShowMenu
            if (mShowMenu) {
                //弹出玩法菜单：关闭cocos、历史记录窗口，软键盘关闭
                if (lotExpl.isExpanded) {
                    lotExpl.collapse(false)
                }
                KeyBoards.hideSoftInput(this)
            }
            lotMenuLl.visible(mShowMenu)
        }
        lotMenuLl.setOnClickListener {
            mShowMenu = false
            lotMenuLl.visible(false)
        }
    }
    //endregion

    //region 经典：玩法下标
    private var mPlayLayer1: Int = 0
    private var mPlayGroup: Int = 0
    private var mPlayLayer2: Int = 0
    private var mPlayId: Int = 0

    //读取该cb选中下标
    private fun playParamsCache(gameId: Int) {
        mPlayLayer1 = tCache.playLayer1Cache4GameId(gameId)
        mPlayGroup = tCache.playGroupCache4GameId(gameId)
        mPlayLayer2 = tCache.playLayer2Cache4GameId(gameId)
        mPlayId = tCache.playIdCache4GameId(gameId)
    }
    //endregion

    //region 一级玩法
    fun updatePlayLayer1List(betTypeDatas: GameBetTypeData?) {
        val padding = Sizes.dp2px(2.5f)
        val margin = Sizes.dp2px(8f)
        lotMenuPlayLayer1Rv.setListOrUpdate(betTypeDatas) {
            object : BaseSelectedQuickAdapter<PlayLayer1Item, BaseViewHolder>(
                R.layout.text_single_item,
                it?.toMutableList()
            ) {
                override fun convert(holder: BaseViewHolder, item: PlayLayer1Item) {
                    holder.getView<TextView>(R.id.text_common_tv).run {
                        margin(margin * 3, margin / 2, margin * 3, margin / 2)
                        gravity = Gravity.CENTER
                        setPadding(padding)
                        text = item.name
                        setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.lot_jd_play_menu_selector
                            )
                        )
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                        setBackgroundResource(R.drawable.lot_jd_play_menu_layer1_selector)
                        isSelected = isSelected(holder)
                    }
                }

                override fun convert(
                    holder: BaseViewHolder,
                    item: PlayLayer1Item,
                    payloads: List<Any>,
                ) {
                    holder.getView<TextView>(R.id.text_common_tv).isSelected = isSelected(holder)
                }
            }.apply {
                setOnItemClickListener { _: BaseQuickAdapter<*, *>, _: View, position: Int ->
                    //一级玩法选中
                    notifySelectedPositionWithPayLoads(position)
                    updatePlayLayer2List(position, betTypeDatas?.get(position))
                }
            }
        }
        lotMenuPlayLayer1Rv.adapter?.let {
            if (it is BaseSelectedQuickAdapter<*, *>) it.notifySelectedPosition(
                mPlayLayer1)
        }
        updatePlayLayer2List(mPlayLayer1,
            if (betTypeDatas.validIndex(mPlayLayer1)) betTypeDatas?.get(mPlayLayer1) else null)
    }
    //endregion

    //region 二级玩法、二级玩法组
    private var mPlayLayer1Tmp = -1
    private fun updatePlayLayer2List(playLayer1: Int, betTypeItem: PlayLayer1Item?) {
        mPlayLayer1Tmp = playLayer1
        val padding = Sizes.dp2px(4f)
        val margin = Sizes.dp2px(8f)
        lotMenuPlayLayer2Rv.setListOrUpdate(betTypeItem?.list) {
            object : BaseSelectedQuickAdapter<PlayGroupItem, BaseViewHolder>(
                R.layout.lot_jd_play_group_item,
                it?.toMutableList()
            ) {
                override fun convert(groupHolder: BaseViewHolder, item: PlayGroupItem) {
                    val groupPosition = groupHolder.adapterPosition
                    groupHolder.setText(R.id.lot_jd_play_group_name_tv, item.name + "：")
                    groupHolder.getView<RecyclerView>(R.id.lot_jd_play_group_rv).run {
                        layoutManager =
                            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
                        setListOrUpdate(item.list) {
                            object : BaseSelectedQuickAdapter<PlayLayer2Item, BaseViewHolder>(
                                R.layout.text_single_item,
                                it?.toMutableList()
                            ) {
                                override fun convert(
                                    holder: BaseViewHolder,
                                    item: PlayLayer2Item,
                                ) {
                                    holder.getView<TextView>(R.id.text_common_tv).run {
                                        margin(margin * 2, margin / 2, margin * 2, margin / 2)
                                        gravity = Gravity.CENTER
                                        setPadding(padding)
                                        text = item.betName
                                        setTextColor(
                                            ContextCompat.getColorStateList(
                                                context,
                                                R.color.lot_jd_play_menu_selector
                                            )
                                        )
                                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
                                        setBackgroundResource(R.drawable.lot_jd_play_menu_layer2_selector)
                                        isSelected =
                                            isSelected(holder) && mPlayGroup == groupPosition && mPlayLayer1Tmp == mPlayLayer1
                                    }
                                }

                                override fun convert(
                                    holder: BaseViewHolder,
                                    item: PlayLayer2Item,
                                    payloads: List<Any>,
                                ) {
                                    holder.getView<TextView>(R.id.text_common_tv).isSelected =
                                        isSelected(holder) && (mPlayGroup == groupPosition) && (mPlayLayer1Tmp == mPlayLayer1)
                                }
                            }.apply {
                                setOnItemClickListener { _: BaseQuickAdapter<*, *>, _: View, position: Int ->
                                    //选中玩法：更新一级玩法下标、二级玩法组下标、二级玩法下标
                                    if (mPlayLayer1 == mPlayLayer1Tmp && mPlayGroup == groupPosition && mPlayLayer2 == position) return@setOnItemClickListener
                                    if (mPlayLayer1 != mPlayLayer1Tmp) mPlayLayer1 = mPlayLayer1Tmp
                                    if (mPlayGroup != groupPosition) {
                                        val preGroupPosition = mPlayGroup
                                        mPlayGroup = groupPosition
                                        lotMenuPlayLayer2Rv.adapter?.notifyItemChanged(
                                            preGroupPosition, PAY_LOADS_SELECTED)
                                    }
                                    notifySelectedPositionWithPayLoads(position)
                                    mPlayLayer2 = position
                                }
                            }
                        }
                    }

                    if (groupPosition == mPlayGroup)
                        groupHolder.getView<RecyclerView>(R.id.lot_jd_play_group_rv).adapter?.let {
                            if (it is BaseSelectedQuickAdapter<*, *>) it.notifySelectedPosition(
                                mPlayLayer2)
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
}
