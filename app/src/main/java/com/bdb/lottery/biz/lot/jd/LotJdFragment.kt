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
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.biz.lot.jd.duplex.adapter.LotDuplexAdapter
import com.bdb.lottery.biz.lot.jd.single.SingleTextWatcher
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.const.GAME
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.dialog.ConfirmDialog
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.adapterPattern.TextWatcherAdapter
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.thread.Threads
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.lot_jd_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class LotJdFragment : BaseFragment(R.layout.lot_jd_fragment) {
    private val vm by viewModels<LotJdViewModel>()

    private var mMultiple: Int = 1
    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    private var mBetTypeId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mGameId = it.getInt(EXTRA.ID_GAME_EXTRA)
            mGameType = it.getInt(EXTRA.TYPE_GAME_EXTRA)
            mGameName = it.getString(EXTRA.NAME_GAME_EXTRA)
            mBetTypeId = it.getInt(EXTRA.ID_PLAY_EXTRA)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestDatas()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.cacheMoneyUnit(mGameId, mBetTypeId, mMoneyUnit)
    }
    //endregion

    //region 删除重复错误号码、下注点击事件监听，单位弹窗、倍数窗口初始化
    private fun initView() {
        skin4GameType(mGameType)
        lot_jd_duplex_rv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //删除重复、错误号码
        lot_jd_remove_repeat_nums_tv.setOnClickListener {
            mTextWatcher?.filterRepeatNdErrorNums(
                lot_jd_single_input_et.text.toString().trim(), false
            )
        }
        //清空号码
        initClearNums()
        //投注参数
        initAmountUnitPopWin()//金额单位
        log_jd_money_unit_tv.setOnClickListener {
            mUnitPopWindow.show(log_jd_money_unit_tv)
        }

        //倍数
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
                    reCalculateAmountNdSingleMoney()
                }
            }
        })

        //下注
        lot_jd_direct_betting_tv.setOnClickListener {
            val lotBlock: (String) -> Unit = {
                vm.verifyNdGenLotParams(
                    mGameId, mBetTypeId, mGameName,
                    it,
                    mMultiple,
                    mMoneyUnit,
                    vm.getDigit(mNeedDigit, getSelectedDigit()),
                    mNeedDigit,
                    verifyDigit(mNeedDigit, mAtLeastDigit),
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
            if (MODE_SINGLE == mMode) {
                //单式
                mTextWatcher?.filterRepeatNdErrorNums(
                    lot_jd_single_input_et.text.toString().trim(), false, lotBlock
                )
            } else {
                //复式
                val makeBetNumStr = BetCenter.makeBetNumStr(
                    lot_jd_duplex_rv.adapter?.let { if (it is LotDuplexAdapter) it.getAllSelectedNums() else null },
                    mIsBuildAll,
                    mDigitsTitle
                )
                try {
                    mNoteCount = BetCenter.computeStakeCount(
                        makeBetNumStr,
                        mGameType,
                        mBetTypeId,
                        vm.getDigit(mNeedDigit, getSelectedDigit())
                    )
                } catch (e: Exception) {
                    val message = e.message
                    if (!message.isSpace()) toast.showWarning(message)
                    return@setOnClickListener
                }
                lotBlock.invoke(makeBetNumStr)
            }

        }

        //玩法菜单数据获取失败，点击重新请求数据
        aliveActivity<LotActivity>()?.mPlayLoadingLayout?.setRetryListener {
            vm.getBetType(mGameId)
        }

        val listener = CompoundButton.OnCheckedChangeListener { _, _ ->
            mTextWatcher?.setDigit(
                vm.getDigit(
                    mNeedDigit,
                    getSelectedDigit()
                )
            )
        }
        lot_jd_bet_digit_ge_cb.setOnCheckedChangeListener(listener)
        lot_jd_bet_digit_shi_cb.setOnCheckedChangeListener(listener)
        lot_jd_bet_digit_bai_cb.setOnCheckedChangeListener(listener)
        lot_jd_bet_digit_qian_cb.setOnCheckedChangeListener(listener)
        lot_jd_bet_digit_wan_cb.setOnCheckedChangeListener(listener)
    }
    //endregion

    //region 彩种大类换肤
    private fun skin4GameType(gameType: Int) {
        //玩法说明、投注区域、投注总额
        when (gameType) {
            GAME.TYPE_GAME_PK8, GAME.TYPE_GAME_PK10 -> {
                val bgRes = ContextCompat.getColor(requireContext(), R.color.color_skin_pk_bg_jd)
                lot_jd_root_ctl.setBackgroundColor(bgRes)
                lot_jd_desc_divide_view.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_play_desc_skin_pk_divide
                    )
                )
                lot_jd_bet_info_expl.setBackgroundColor(bgRes)
            }
            GAME.TYPE_GAME_K3 -> {
                val bgRes = R.drawable.lot_jd_k3_skin_bg
                val bgColor = ContextCompat.getColor(requireContext(), R.color.color_skin_k3_bg_jd)
                lot_jd_root_ctl.setBackgroundResource(bgRes)
                lot_jd_desc_divide_view.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_play_desc_skin_k3_divide
                    )
                )
                lot_jd_bet_info_expl.setBackgroundColor(bgColor)
            }
            else -> {
                val bgRes = ContextCompat.getColor(requireContext(), R.color.color_bg)
                lot_jd_root_ctl.setBackgroundColor(bgRes)
                lot_jd_desc_divide_view.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_play_desc_skin_divide
                    )
                )
                lot_jd_bet_info_expl.setBackgroundColor(bgRes)
            }
        }

    }
    //endregion

    //region 请求参数：初始化彩票、玩法配置接口，金额单位缓存
    @Inject
    lateinit var lotPlayDescDialog: LotPlayDescDialog
    private fun requestDatas() {
        //初始化金额单位：根据gameId读取金额单位缓存，没有缓存默认1（元）
        vm.initAmountUnit(mGameId, mBetTypeId) {
            mMoneyUnit = it ?: 1
            log_jd_money_unit_tv.text = Converts.unit2String(mMoneyUnit)
        }
        vm.initGame(mGameId)//初始化彩票
        vm.getBetType(mGameId)//玩法配置接口
        lot_jd_play_desc_tv.setOnClickListener {
            vm.cachePreHowToPlay(mGameType, mBetTypeId) { playDesc: String? ->
                playDesc?.let {
                    //玩法说明弹窗
                    lotPlayDescDialog.setGameType(mGameType)
                    lotPlayDescDialog.setPlayDescContent(it)
                    lotPlayDescDialog.show(childFragmentManager)
                }
            }
        }
    }
    //endregion

    @Inject
    lateinit var accountManager: AccountManager
    private var mNeedDigit: Boolean? = null//是否需要验证位置
    private var mAtLeastDigit: Int? = null//最少位置
    private var mTextWatcher: SingleTextWatcher? = null
    private var mNoteCount: Int = 0//投注数量
    private var mCanPutBasket: Boolean = false//能否加入购物篮
    private var mAmount: Double = 0.0//总额
    private var mSingleNumCount: Int = 0//单注号码数
    private var mIsBuildAll: Boolean? = null
    private var mDigitsTitle: String? = null
    override fun observe() {
        //账户余额、今日彩票盈亏
        ob(accountManager.mUserBalance.getLiveData()) {
            lot_jd_account_balance_tv.text = String.format(
                getString(R.string.lot_jd_account_balance_text),
                (it?.center ?: 0.0).money()
            )
            val profit = it?.getTodayJdProfit() ?: 0.0
            lot_jd_today_profit_tv.text = Html.fromHtml(
                String.format(
                    getString(R.string.lot_jd_today_loss_profit_text),
                    (profit).money()
                        .h5Color(if (profit > 0) "#FA4529" else if (profit < 0) "#66CC66" else "#333333")
                )
            )
        }
        //获取玩法配置，更新玩法列表，根据缓存选中玩法，没有缓存默认第一个玩法
        ob(vm.gameBetTypeDataLd.getLiveData()) {
            aliveActivity<LotActivity>()?.updatePlayList(it)
        }

        //数据库查找玩法成功
        ob(vm.subPlayMethodLd.getLiveData()) {
            aliveActivity<LotActivity>()?.updateHistoryLabel(it?.parent_play_method ?: 0)
            //单复式
            mMode = if (it?.subPlayMethodDesc?.isdanshi != false) MODE_SINGLE else MODE_DUPLEX
            mSingleNumCount = it?.subPlayMethodDesc?.single_num_counts ?: 0//单式
            mIsBuildAll = it?.subPlayMethodDesc?.isBuildAll
            mDigitsTitle = it?.subPlayMethodDesc?.digit_titles
            mNeedDigit = it?.subPlayMethodDesc?.is_need_show_weizhi == true
            if (mNeedDigit == true) {
                mAtLeastDigit = it?.subPlayMethodDesc?.atleast_wei_shu?.toInt()
                setAtLeastDigit(mAtLeastDigit)
            }

            lot_jd_bet_digit_ll.visible(mNeedDigit == true)

            mTextWatcher?.onBetChange(
                mSingleNumCount,
                vm.getDigit(mNeedDigit, getSelectedDigit())
            )
            if (null == mTextWatcher) {
                mTextWatcher = vm.createSingleTextWatcher(
                    mGameType,
                    mBetTypeId,
                    lot_jd_single_input_et,
                    mSingleNumCount,
                    vm.getDigit(mNeedDigit, getSelectedDigit()),
                    {
                        Threads.retrofitUIThread {
                            val canPutBasket = it > 0
                            if (mCanPutBasket != canPutBasket) {
                                //加入购物篮切换
                                if (canPutBasket) lot_jd_bet_info_expl.expand() else lot_jd_bet_info_expl.collapse(
                                    false
                                )
                                lot_jd_add_to_shopping_bar_tv.setTextSize(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    if (canPutBasket) 12f else 15f
                                )
                                lot_jd_add_to_shopping_bar_tv.text =
                                    getString(if (canPutBasket) R.string.lot_jd_put_shopping_bar else R.string.lot_jd_shopping_bar)
                                mCanPutBasket = canPutBasket
                            }
                            if (mNoteCount != it) {
                                lot_jd_selected_notes_tv.text =
                                    String.format(
                                        getString(R.string.lot_jd_selected_notes_text),
                                        it
                                    )//注数
                                mNoteCount = it
                                reCalculateAmountNdSingleMoney()
                            }
                        }
                    },
                    toast::showError
                )
                lot_jd_single_input_et.addTextChangedListener(mTextWatcher)
            }
            //切换单复式UI
            switchDanFuStyle(mMode == MODE_SINGLE, it)
        }
    }


    //region 构造方法：传递参数gameType、gameId、gameName
    companion object {
        fun newInstance(gameType: Int, gameId: Int, playId: Int, gameName: String?): LotJdFragment {
            val fragment = LotJdFragment()
            val args = Bundle()
            args.putInt(EXTRA.ID_GAME_EXTRA, gameId)
            args.putInt(EXTRA.TYPE_GAME_EXTRA, gameType)
            args.putInt(EXTRA.ID_PLAY_EXTRA, playId)
            args.putString(EXTRA.NAME_GAME_EXTRA, gameName)
            fragment.arguments = args
            return fragment
        }
    }
    //endregion

    //region 金额单位popup
    @Inject
    lateinit var mUnitPopWindow: UnitPopWindow
    private var mUnitPattern: String? = null
    private fun setUnitPattern(unitPattern: String) {
        mUnitPattern = unitPattern
        mUnitPopWindow.setPattern(unitPattern)
    }

    private var mMoneyUnit: Int = 1//1：元，2角，3分，4厘（默认元）
    private fun initAmountUnitPopWin() {
        mUnitPopWindow.init { view: View ->
            mUnitPopWindow.dismiss()
            when (view.id) {
                R.id.lot_jd_money_unit_yuan_tv -> mMoneyUnit = 1
                R.id.lot_jd_money_unit_jiao_tv -> mMoneyUnit = 2
                R.id.lot_jd_money_unit_fen_tv -> mMoneyUnit = 3
                R.id.lot_jd_money_unit_li_tv -> mMoneyUnit = 4
            }
            log_jd_money_unit_tv.text = Converts.unit2String(mMoneyUnit)
            reCalculateAmountNdSingleMoney()
        }
    }
    //endregion

    //region 清空号码
    @Inject
    lateinit var mConfirmDialog: ConfirmDialog
    private fun initClearNums() {
        //清空确认弹窗
        mConfirmDialog.contentText("是否清除已选择号码").onConfirm {
            clearNums()
        }
        lot_jd_input_clear_iv.setOnClickListener {
            mConfirmDialog.show(childFragmentManager)
        }
    }

    //投注成功、点击清空按钮、切换玩法
    private fun clearNums() {
        if (MODE_SINGLE == mMode) {
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

    //region 下注倒计时状态更新
    private var mClosed = false
    fun updateLotStatus(closed: Boolean) {
        if (mClosed == closed) return
        lot_jd_direct_betting_tv.text =
            getString(if (closed) R.string.lot_jd_closed else R.string.lot_jd_direct_betting)
        lot_jd_direct_betting_tv.isActivated = closed
        mClosed = closed
    }
    //endregion

    //region 更新玩法时：title、玩法配置、投注单位更新
    private var mSelectedBetItem: BetItem? = null
    fun onBetSelected(item: BetItem?) {
        mSelectedBetItem = item//更新数据
        val playId = item?.betType ?: 0//玩法id
        mTextWatcher?.setPlayId(playId)
        mBetTypeId = playId
        item?.let {
            aliveActivity<LotActivity>()?.updateMarqueeView(it.getPlayTitle())//更新玩法名称，title刷新
            vm.getLocalBetType(playId)//获取玩法配置
            setUnitPattern(it.pattern)//更新投注单位
            //玩法说明
            vm.cachePreHowToPlay(mGameType, mBetTypeId) { playDesc: String? ->
                lot_jd_play_desc_tv.text = playDesc ?: "该玩法暂无玩法说明"
            }
        }
    }
    //endregion

    //region 注数、金额单位切换、倍率改变时调用（重新计算：投注总额、理论最高奖金）
    private fun reCalculateAmountNdSingleMoney() {
        mAmount =
            vm.getAmount(mNoteCount, mMoneyUnit, mMultiple)
        lot_jd_total_amount_tv.text =
            Html.fromHtml(
                String.format(
                    getString(R.string.lot_jd_total_money_text),
                    mAmount.money().h5Color("#fa4529")
                )
            ) //总额
        lot_jd_max_bonus_tv.text = Html.fromHtml(
            String.format(
                getString(R.string.lot_jd_max_bonus_text),
                (vm.getSingleMoney(mSelectedBetItem) * mMultiple / Converts.unit2Params(mMoneyUnit)).money()
                    .h5Color("#F7831C")
            )
        )
        //最高理论奖金
    }
    //endregion

    //region digit万、千、百、十个
    private fun setAtLeastDigit(atLeastDigit: Int?) {
        val visible = null != atLeastDigit
        lot_jd_bet_digit_ll.visible(visible)
        if (visible) {
            lot_jd_bet_digit_ge_cb.isChecked = atLeastDigit!! >= 1
            lot_jd_bet_digit_shi_cb.isChecked = atLeastDigit >= 2
            lot_jd_bet_digit_bai_cb.isChecked = atLeastDigit >= 3
            lot_jd_bet_digit_qian_cb.isChecked = atLeastDigit >= 4
            lot_jd_bet_digit_wan_cb.isChecked = atLeastDigit >= 5
        }
    }

    private fun getSelectedDigit(): String {
        if (mNeedDigit != true) return ""
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

    private fun verifyDigit(needDigit: Boolean?, atLeastDigit: Int?): String? {
        if (null == atLeastDigit || needDigit != true) return null
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

    //region 切换单式、复式
    private val MODE_SINGLE = 0//单式
    private val MODE_DUPLEX = 1//复式
    private var mMode = MODE_DUPLEX//模式
    private fun switchDanFuStyle(isSingleStyle: Boolean, subPlayMethod: SubPlayMethod?) {
        mMode = if (isSingleStyle) MODE_SINGLE else MODE_DUPLEX
        clearNums()//清空号码
        //单式
        lot_jd_bet_single_ll.visible(isSingleStyle)
        //复式
        lot_jd_duplex_rv.visible(!isSingleStyle)
        if (!isSingleStyle) {
            val ballGroupCount: Int = subPlayMethod?.subPlayMethodDesc?.ball_groups_counts ?: 0
            val lotDuplexDatas = mutableListOf<LotDuplexData>()
            val ballTextList = subPlayMethod?.subPlayMethodDesc?.ball_text_list?.split(",")?.let {
                if (GAME.TYPE_GAME_K3 == mGameType) it.sortedWith { s: String, s1: String ->
                    val c1 = if (s.isDigit()) 1 else -1
                    val c2 = if (s1.isDigit()) 1 else -1
                    c1 - c2
                } else it
            }
            if (ballGroupCount > 0) {
                val titles = subPlayMethod?.subPlayMethodDesc?.ball_groups_item_title?.split(",")
                for (i in 0 until ballGroupCount) {
                    lotDuplexDatas.add(
                        LotDuplexData(
                            titles?.get(i),
                            subPlayMethod?.subPlayMethodDesc?.item_ball_num_counts ?: 0,
                            ballTextList,
                            dxdsVisible = subPlayMethod?.subPlayMethodDesc?.is_show_type_select == true,
                            hotVisible = false,
                            leaveVisible = false,
                            isStartZero = subPlayMethod?.subPlayMethodDesc?.is_start_zero == true,
                            zeroVisible = subPlayMethod?.subPlayMethodDesc?.isShowZero == true,
                        )
                    )
                }
            }
            lot_jd_duplex_rv.adapter?.let {
                //刷新复式列表
                if (it is LotDuplexAdapter) it.notifyChangeWhenPlayChange(
                    mBetTypeId,
                    ballTextList,
                    lotDuplexDatas
                )
            } ?: let {
                //初始化复式列表
                lot_jd_duplex_rv.adapter =
                    LotDuplexAdapter(
                        mGameType,
                        mBetTypeId,
                        ballTextList,
                        lotDuplexDatas
                    )
            }
        }
    }
    //endregion
}