package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.globallivedata.AccountManager
import com.bdb.lottery.biz.lot.BetCenter
import com.bdb.lottery.biz.lot.UnitPopWindow
import com.bdb.lottery.biz.lot.activity.LotActivity
import com.bdb.lottery.biz.lot.dialog.playdesc.LotPlayDescDialog
import com.bdb.lottery.biz.lot.jd.duplex.adapter.LotDuplexAdapter
import com.bdb.lottery.biz.lot.jd.single.SingleTextWatcher
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.HotData
import com.bdb.lottery.datasource.lot.data.jd.LeaveData
import com.bdb.lottery.dialog.ConfirmDialog
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.adapterPattern.TextWatcherAdapter
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.thread.Threads
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.lot_jd_fragment.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LotJdFragment : BaseFragment(R.layout.lot_jd_fragment) {
    private val vm by viewModels<LotJdViewModel>()

    //region 构造方法：传递参数gameType、gameId、gameName
    companion object {
        fun newInstance(
            gameType: Int,
            gameId: Int,
            betTypeId: Int,
            gameName: String?,
        ): LotJdFragment {
            val fragment = LotJdFragment()
            val args = Bundle()
            args.putInt(EXTRA.ID_GAME_EXTRA, gameId)
            args.putInt(EXTRA.TYPE_GAME_EXTRA, gameType)
            args.putInt(EXTRA.ID_BET_TYPE_EXTRA, betTypeId)
            args.putString(EXTRA.NAME_GAME_EXTRA, gameName)
            fragment.arguments = args
            return fragment
        }
    }
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.initExtraArgs(arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestDatas()
    }

    private fun initView() {
        skin4GameType()//换肤
        initDuplexRv()//复式：号码球列表初始化控件
        initMoneyUnit()//金额单位

        playMenuRetryWhenErr()//玩法菜单数据获取失败，点击重新请求数据

        playDescClick()//玩法说明
        hotLeaveClick()//冷热遗漏
        digitClick()//digit：万位、千位、百位、十位、个位（任选玩法）
        repeatNdErrorClick()//删除重复、错误号码
        clearNumsCLick() //清空号码
        moneyUnitClick()//金额单位
        multipleClick()//倍数
        lotClick()//下注
    }

    //region 换肤
    private fun skin4GameType() {
        lot_jd_play_hot_tv.setBackgroundResource(if (vm.isPK()) R.drawable.lot_jd_hot_hot_leave_pk_bg_selector else R.drawable.lot_jd_hot_hot_leave_bg_selector)
        lot_jd_play_leave_tv.setBackgroundResource(if (vm.isPK()) R.drawable.lot_jd_hot_hot_leave_pk_bg_selector else R.drawable.lot_jd_hot_hot_leave_bg_selector)
        lot_jd_desc_divide_view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                vm.betAreaTopDivideSkinByGameType()
            )
        )//投注区域顶部分割线
        val bgRes = ContextCompat.getColor(requireContext(), vm.betAreaBgResSkinByGameType())
        lot_jd_bet_info_expl.setBackgroundColor(bgRes)//底部投注信息栏
        //投注区域背景
        if (vm.isK3()) {
            lot_jd_root_ctl.setBackgroundResource(R.drawable.lot_jd_k3_skin_bg)
        } else {
            lot_jd_root_ctl.setBackgroundColor(bgRes)
        }
    }
    //endregion

    //region 复式：投注区域
    private fun initDuplexRv() {
        lot_jd_duplex_rv.itemAnimator = null//删除不必要的动画
        lot_jd_duplex_rv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
    //endregion

    //region 网络数据失败：玩法菜单数据获取失败，点击重新请求数据
    private fun playMenuRetryWhenErr() {
        aliveActivity<LotActivity>()?.mPlayLoadingLayout?.setRetryListener {
            vm.netBetType()
        }
    }
    //endregion

    //region 金额单位

    //region 金额单位：缓存
    private var mMoneyUnit = 1;//1：元，2角，3分，4厘（默认元）
    private fun initMoneyUnit() {
        mMoneyUnit = vm.moneyUnitCache()
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.cacheMoneyUnit(mMoneyUnit)
    }
    //endregion

    //region 金额单位文本框（1：元，2角，3分，4厘（默认元））
    private fun renderMoneyUnit(moneyUnit: Int) {
        log_jd_money_unit_tv.text = Converts.unit2String(moneyUnit)
    }
    //endregion

    //region pattern(1,2,3,4)->元，角，分，厘
    private var mUnitPattern: String? = null
    private fun setUnitPattern(unitPattern: String) {
        mUnitPattern = unitPattern
        mUnitPopWindow.setPattern(unitPattern)
    }
    //endregion
    //endregion

    //region 清空投注（投注成功、点击清空按钮、切换玩法）
    private fun clearNums() {
        if (vm.isSingleMode()) {
            //单式
            lot_jd_single_input_et.setText("")
        } else {
            //复式
            lot_jd_duplex_rv.adapter?.let {
                if (it is LotDuplexAdapter) it.clearSelectedNums()
            }
        }
    }
    //endregion

    //region 网络请求：初始化彩票、玩法配置接口
    private fun requestDatas() {
        vm.netInitGame()//初始化彩票
        vm.netBetType()//玩法配置接口
        vm.netHot()//冷热
        vm.netLeave()//遗漏
    }
    //endregion

    //region 监听数据：全局用户余额，玩法配置，地数据库查询玩法信息，玩法切换
    @Inject
    lateinit var accountManager: AccountManager
    private var mTextWatcher: SingleTextWatcher? = null
    private var mNoteCount: Int = 0//投注数量
    private var mCanPutBasket: Boolean = false//能否加入购物篮
    private var mAmount: Double = 0.0//总额
    private var mDuplexNums: String? = null//复式投注号码
    override fun observe() {
        //投注信息栏（账户余额、今日彩票盈亏）
        ob(accountManager.mUserBalance.getLiveData()) {
            lot_jd_account_balance_tv.text =
                it.jdAccountBalance(getString(R.string.lot_jd_account_balance_text))
            lot_jd_today_profit_tv.text =
                it.jdTodayProfitH5(getString(R.string.lot_jd_today_loss_profit_text))
        }
        //获取玩法配置，更新玩法列表，根据缓存选中玩法，没有缓存默认第一个玩法
        ob(vm.gameBetTypeDataLd.getLiveData()) {
            aliveActivity<LotActivity>()?.updatePlayList(it)
        }

        //digit显示隐藏
        ob(vm.needDigitLd.getLiveData()) {
            lot_jd_bet_digit_ll.visible(it)
        }

        //digit位数
        ob(vm.atLeastDigitLd.getLiveData()) {
            renderAtLeastDigit(it)
        }

        //开奖历史形态
        ob(vm.parentPlayMethodLd.getLiveData()) {
            aliveActivity<LotActivity>()?.updateHistoryLabel(it)
        }

        //单式：玩法切换
        ob(vm.singleNumCountsLd.getLiveData()) {
            mTextWatcher?.onBetChange(
                it,
                vm.getDigit(getSelectedDigits())
            )
            if (null == mTextWatcher) {
                mTextWatcher = vm.createSingleTextWatcher(
                    lot_jd_single_input_et,
                    it,
                    vm.getDigit(getSelectedDigits()),
                    {
                        Threads.retrofitUIThread {
                            toggleAmountBanner(it)
                        }
                    },
                    toast::showError
                )
                lot_jd_single_input_et.addTextChangedListener(mTextWatcher)
            }
        }

        //复式：玩法切换
        ob(vm.duplexBallDatasLd.getLiveData()) { lotDuplexLd ->
            lot_jd_duplex_rv.adapter?.let {
                //刷新复式列表
                if (it is LotDuplexAdapter) {
                    it.notifyChangeWhenPlayChange(lotDuplexLd)
                }
            } ?: let {
                //初始化复式列表
                lot_jd_duplex_rv.adapter =
                    LotDuplexAdapter(
                        lotDuplexLd.gameType,
                        lotDuplexLd.betTypeId,
                        lotDuplexLd.ballTextList,
                        lotDuplexLd.lotDuplexDatas,
                        vm.getOddInfoMap(),
                        vm.getHotDatas(),
                        vm.getLeaveDatas()
                    ) {
                        mDuplexNums = vm.makeBetNumStr(it)
                        var count = 0
                        try {
                            count = vm.computeDuplexCount(mDuplexNums, vm.getDigit(getSelectedDigits()))
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                        toggleAmountBanner(count)
                    }
            }
            lot_jd_duplex_rv.adapter.let {
                if (it is LotDuplexAdapter) {
                    it.renderLhhOddInfo(vm.getOddInfoMap())
                }
            }
        }

        //玩法切换
        ob(vm.singleModeLd.getLiveData()) {
            //单式
            lot_jd_bet_single_ll.visible(it)
            //复式
            lot_jd_duplex_rv.visible(!it)
            //冷热遗漏
            val visibleHotLeave = vm.visibleHotLeave()
            lot_jd_play_hot_tv.visible(visibleHotLeave)
            lot_jd_play_leave_tv.visible(visibleHotLeave)
        }

        //官方验证：标题栏popup菜单
        ob(vm.gameLd.getLiveData()) {
            aliveActivity<LotActivity>()?.visibleMenuPopVerifyBanner(it.startExplain, it.startSign)

        }

        //奖金
        ob(vm.oddInfoMapLd.getLiveData()) { oddInfo: MutableMap<String, Double> ->
            lot_jd_duplex_rv.adapter?.let {
                if (it is LotDuplexAdapter) {
                    it.renderLhhOddInfo(oddInfo)
                }
            }
        }

        //冷热
        ob(vm.hotLd.getLiveData()) { hot: HotData ->
            if (mHotLeave == false) {
                lot_jd_duplex_rv.adapter?.let {
                    if (it is LotDuplexAdapter) {
                        it.renderHot(hot)
                    }
                }
            }
        }

        //遗漏
        ob(vm.leaveLd.getLiveData()) { leave: LeaveData ->
            if (mHotLeave == true) {
                lot_jd_duplex_rv.adapter?.let {
                    if (it is LotDuplexAdapter) {
                        it.renderLeave(leave)
                    }
                }
            }
        }
    }
    //endregion

    //region 购彩篮、投注信息栏
    private fun toggleAmountBanner(noteCount: Int) {
        val canPutBasket = noteCount > 0
        if (mCanPutBasket != canPutBasket) {
            //加入购物篮切换
            if (canPutBasket) lot_jd_bet_info_expl.expand(false) else lot_jd_bet_info_expl.collapse(false)
            lot_jd_add_to_shopping_bar_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, if (canPutBasket) 12f else 15f)
            lot_jd_add_to_shopping_bar_tv.text = getString(if (canPutBasket) R.string.lot_jd_put_shopping_bar else R.string.lot_jd_shopping_bar)
            mCanPutBasket = canPutBasket
        }
        if (mNoteCount != noteCount) {
            mNoteCount = noteCount
            renderBetInfoBanner()
        }
    }
    //endregion

    //region 倒计时：下注状态更新
    private var mClosed = false
    fun updateLotStatus(closed: Boolean) {
        if (mClosed == closed) return
        lot_jd_direct_betting_tv.text = getString(if (closed) R.string.lot_jd_closed else R.string.lot_jd_direct_betting)
        lot_jd_direct_betting_tv.isActivated = closed
        mClosed = closed
    }
    //endregion

    //region 切换玩法：title、玩法配置、投注单位更新
    private var mSelectedBetItem: BetItem? = null
    fun onBetSelected(item: BetItem?) {
        clearNums()
        item?.let {
            val betTypeId = it.betType//玩法id
            mSelectedBetItem = it//更新数据
            mTextWatcher?.setBetTypeId(betTypeId)
            aliveActivity<LotActivity>()?.updateMarqueeView(it.getJdPlayTitle())//更新玩法名称，title刷新
            setUnitPattern(it.pattern)//更新投注单位
            vm.dbBetType(betTypeId)//获取玩法配置
            //玩法说明
            vm.cachePreHowToPlay { _, playDesc: String? ->
                lot_jd_play_desc_tv.text = playDesc ?: "该玩法暂无玩法说明"
            }
        }
    }
    //endregion

    //region 投注信息栏：注数，总额，最高理论奖金（注数、金额单位切换、倍率改变）
    private fun renderBetInfoBanner() {
        //注数
        lot_jd_selected_notes_tv.text =
            String.format(
                getString(R.string.lot_jd_selected_notes_text),
                mNoteCount
            )
        //总额
        mAmount =
            vm.getAmount(mNoteCount, mMoneyUnit, mMultiple)
        lot_jd_total_amount_tv.text =
            Html.fromHtml(
                String.format(
                    getString(R.string.lot_jd_total_money_text),
                    mAmount.money().h5Color("#fa4529")
                )
            )
        //最高理论奖金
        lot_jd_max_bonus_tv.text = Html.fromHtml(
            String.format(
                getString(R.string.lot_jd_max_bonus_text),
                (vm.getSingleMoney(
                    mDuplexNums,
                    mSelectedBetItem
                ) * mMultiple / Converts.unit2Params(mMoneyUnit)).money()
                    .h5Color("#F7831C")
            )
        )
    }
    //endregion

    //region digit：万、千、百、十、个
    private fun renderAtLeastDigit(atLeastDigit: Int) {
        lot_jd_bet_digit_ge_cb.isChecked = atLeastDigit >= 1
        lot_jd_bet_digit_shi_cb.isChecked = atLeastDigit >= 2
        lot_jd_bet_digit_bai_cb.isChecked = atLeastDigit >= 3
        lot_jd_bet_digit_qian_cb.isChecked = atLeastDigit >= 4
        lot_jd_bet_digit_wan_cb.isChecked = atLeastDigit >= 5
    }

    private fun getSelectedDigits(): String {
        if (!vm.needDigit()) return ""
        val sb = StringBuilder()
        if (lot_jd_bet_digit_wan_cb.isChecked) {
            sb.append("0").append(",")
        }
        if (lot_jd_bet_digit_qian_cb.isChecked) {
            sb.append("1").append(",")
        }
        if (lot_jd_bet_digit_bai_cb.isChecked) {
            sb.append("2").append(",")
        }
        if (lot_jd_bet_digit_shi_cb.isChecked) {
            sb.append("3").append(",")
        }
        if (lot_jd_bet_digit_ge_cb.isChecked) {
            sb.append("4").append(",")
        }

        //移除最后一个“,”
        if (sb.length > 1) {
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    private fun verifyDigit(): String? {
        val needDigit = vm.needDigit()
        val atLeastDigit = vm.atLeastDigit()
        if (null == atLeastDigit || !needDigit) return null
        var checkedCount: Short = 0
        if (lot_jd_bet_digit_wan_cb.isChecked) {
            checkedCount++
        }
        if (lot_jd_bet_digit_qian_cb.isChecked) {
            checkedCount++
        }
        if (lot_jd_bet_digit_bai_cb.isChecked) {
            checkedCount++
        }
        if (lot_jd_bet_digit_shi_cb.isChecked) {
            checkedCount++
        }
        if (lot_jd_bet_digit_ge_cb.isChecked) {
            checkedCount++
        }
        return if (checkedCount >= atLeastDigit) null else "请至少选择${atLeastDigit}个位置"
    }
    //endregion

    //region 点击：玩法说明
    @Inject
    lateinit var lotPlayDescDialog: LotPlayDescDialog
    private fun playDescClick() {
        lot_jd_play_desc_tv.setOnClickListener {
            vm.cachePreHowToPlay { gameType: Int, playDesc: String? ->
                playDesc?.let {
                    //玩法说明弹窗
                    lotPlayDescDialog.setGameType(gameType).setPlayDescContent(it)
                        .show(childFragmentManager)
                }
            }
        }
    }
    //endregion

    //region 点击：冷热遗漏
    private var mHotLeave: Boolean? = null//false：显示冷热，true：显示遗漏，null：都不显示
    private fun hotLeaveClick() {
        lot_jd_play_hot_tv.setOnClickListener {
            vm.netHot()
            lot_jd_duplex_rv.adapter?.let {
                if (it is LotDuplexAdapter) {
                    mHotLeave = if (mHotLeave == false) null else false
                    it.hotLeaveVisible(mHotLeave)
                    lot_jd_play_hot_tv.select(mHotLeave == false)
                    lot_jd_play_leave_tv.select(false)
                }
            }
        }

        lot_jd_play_leave_tv.setOnClickListener {
            vm.netLeave()
            lot_jd_duplex_rv.adapter?.let {
                if (it is LotDuplexAdapter) {
                    mHotLeave = if (mHotLeave == true) null else true
                    it.hotLeaveVisible(mHotLeave)
                    lot_jd_play_hot_tv.select(false)
                    lot_jd_play_leave_tv.select(mHotLeave == true)
                }
            }
        }
    }
    //endregion

    //region 点击：任选玩法选中（万位、千位、百位、十位、个位）
    private fun digitClick() {
        val listener = CompoundButton.OnCheckedChangeListener { _, _ ->
            mTextWatcher?.setDigit(
                vm.getDigit(getSelectedDigits())
            )
        }
        lot_jd_bet_digit_ge_cb.setOnCheckedChangeListener(listener)
        lot_jd_bet_digit_shi_cb.setOnCheckedChangeListener(listener)
        lot_jd_bet_digit_bai_cb.setOnCheckedChangeListener(listener)
        lot_jd_bet_digit_qian_cb.setOnCheckedChangeListener(listener)
        lot_jd_bet_digit_wan_cb.setOnCheckedChangeListener(listener)
    }
    //endregion

    //region 点击：删除重复、错误号码
    private fun repeatNdErrorClick() {
        lot_jd_remove_repeat_nums_tv.setOnClickListener {
            mTextWatcher?.filterRepeatNdErrorNums(
                lot_jd_single_input_et.text.toString().trim(), false
            )
        }
    }
    //endregion

    //region 点击：清空号码
    @Inject
    lateinit var mConfirmDialog: ConfirmDialog
    private fun clearNumsCLick() {
        //清空确认弹窗
        mConfirmDialog.contentText("是否清除已选择号码").onConfirm {
            clearNums()
        }
        lot_jd_input_clear_iv.setOnClickListener {
            mConfirmDialog.show(childFragmentManager)
        }
    }
    //endregion

    //region 点击：金额单位
    @Inject
    lateinit var mUnitPopWindow: UnitPopWindow
    private fun moneyUnitClick() {
        mUnitPopWindow.init { view: View ->
            mUnitPopWindow.dismiss()
            when (view.id) {
                R.id.lot_jd_money_unit_yuan_tv -> mMoneyUnit = 1
                R.id.lot_jd_money_unit_jiao_tv -> mMoneyUnit = 2
                R.id.lot_jd_money_unit_fen_tv -> mMoneyUnit = 3
                R.id.lot_jd_money_unit_li_tv -> mMoneyUnit = 4
            }
            renderMoneyUnit(mMoneyUnit)
            renderBetInfoBanner()
        }

        log_jd_money_unit_tv.setOnClickListener {
            mUnitPopWindow.show(log_jd_money_unit_tv)
        }
    }
    //endregion

    //region 点击：倍数
    private var mMultiple = 1
    private fun multipleClick() {
        lot_jd_multiple_et.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                try {
                    mMultiple = s.toString().toInt()
                    if (mMultiple < 1) {
                        lot_jd_multiple_et.setText("1")
                        lot_jd_multiple_et.setSelection(lot_jd_multiple_et.length())
                    }
                } catch (e: Exception) {
                    lot_jd_multiple_et.setText("1")
                    lot_jd_multiple_et.setSelection(lot_jd_multiple_et.length())
                }
                if (mCanPutBasket) {
                    renderBetInfoBanner()
                }
            }
        })
    }
    //endregion

    //region 点击：下注
    private fun lotClick() {
        lot_jd_direct_betting_tv.setOnClickListener {
            val lotBlock: (String) -> Unit = {
                vm.verifyNdGenLotParams(
                    it,
                    mMultiple,
                    mMoneyUnit,
                    vm.getDigit(getSelectedDigits()),
                    verifyDigit(),
                    mNoteCount,
                    mSelectedBetItem,
                    toast,
                ) { lotParam: LotParam, refreshToken: (String) -> Unit ->
                    aliveActivity<LotActivity>()?.lotByDialog(
                        lotParam,
                        vm.danTiaoTips(mSelectedBetItem),
                        {
                            refreshToken.invoke(it)
                            clearNums()
                        },
                        refreshToken
                    )
                }
            }
            if (vm.isSingleMode()) {
                //单式
                mTextWatcher?.filterRepeatNdErrorNums(
                    lot_jd_single_input_et.text.toString().trim(), false, lotBlock
                )
            } else {
                //复式
                val makeBetNumStr =
                    vm.makeBetNumStr(lot_jd_duplex_rv.adapter?.let { if (it is LotDuplexAdapter) it.getAllSelectedNums() else null })
                try {
                    mNoteCount = vm.computeDuplexCount(makeBetNumStr, getSelectedDigits())
                } catch (e: Exception) {
                    val message = e.message
                    if (!message.isSpace()) toast.showWarning(message)
                    return@setOnClickListener
                }
                lotBlock.invoke(makeBetNumStr)
            }

        }
    }
    //endregion

}