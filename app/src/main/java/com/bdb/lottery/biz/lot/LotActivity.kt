package com.bdb.lottery.biz.lot

import android.Manifest
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
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
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.cocos.TCocos
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayGroupItem
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer1Item
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer2Item
import com.bdb.lottery.dialog.lot.LotDialog
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
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
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
    override var statusbarLight = false;//状态栏是否半透明

    @Inject
    lateinit var tGame: TGame

    @Inject
    lateinit var tLot: TLot
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
        tLot.switchFragment(0, fragments) { lotNumsLabelFl.visible(1 == it) }//默认选中经典

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
        vm.getLotType()
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


    //region 初始化gameId、gameType、gameName、fragment
    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    private lateinit var fragments: Array<Fragment>
    override fun initVar(bundle: Bundle?) {
        super.initVar(bundle)
        mGameId = intent.getIntExtra(EXTRA.ID_GAME_EXTRA, -1)
        mGameType = intent.getIntExtra(EXTRA.TYPE_GAME_EXTRA, -1)
        mGameName = intent.getStringExtra(EXTRA.NAME_GAME_EXTRA)
        if (-1 == mGameId || -1 == mGameType) {
            finish()
        }
        playMenuParamsCache(mGameId)

        fragments = tLot.initFragment(mGameType, mGameId, mGameName, bundle)
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
        if (mShowMenu) {
            mShowMenu = false
            lotMenuLl.visible(false)
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
            val data = tLot.countDownData(it, fragments)
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
    private fun playMenuParamsCache(gameId: Int) {
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
                mPlayLayer1
            )
        }
        updatePlayLayer2List(
            mPlayLayer1,
            if (betTypeDatas.validIndex(mPlayLayer1)) betTypeDatas?.get(mPlayLayer1) else null
        )
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
                                    if (mPlayLayer1 != mPlayLayer1Tmp) {
                                        mPlayLayer1 = mPlayLayer1Tmp
                                    } else {
                                        if (mPlayGroup != groupPosition) {
                                            val preGroupPosition = mPlayGroup
                                            lotMenuPlayLayer2Rv.adapter?.notifyItemChanged(
                                                preGroupPosition, PAY_LOADS_SELECTED
                                            )
                                        }
                                    }
                                    notifySelectedPositionWithPayLoads(position)
                                    mPlayGroup = groupPosition
                                    mPlayLayer2 = position
                                }
                            }
                        }
                    }

                    if (groupPosition == mPlayGroup)
                        groupHolder.getView<RecyclerView>(R.id.lot_jd_play_group_rv).adapter?.let {
                            if (it is BaseSelectedQuickAdapter<*, *>) it.notifySelectedPosition(
                                mPlayLayer2
                            )
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
        token: String,
        multiply: String,
        success: (() -> Unit)? = null,
        error: ((String) -> Unit)? = null,
    ) {
        lotDialog.gameId(mGameId).lotName(mGameName).lotMultiply(multiply)
        lotDialog.show(supportFragmentManager)
    }
    //endregion
}
