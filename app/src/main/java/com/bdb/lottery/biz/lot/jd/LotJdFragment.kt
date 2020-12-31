package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import android.text.Editable
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.biz.lot.LotActivity
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayGroupItem
import com.bdb.lottery.datasource.lot.data.jd.PlayItem
import com.bdb.lottery.dialog.ConfirmDialog
import com.bdb.lottery.extension.setListOrUpdate
import com.bdb.lottery.extension.validIndex
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.adapterPattern.TextWatcherAdapter
import com.bdb.lottery.utils.convert.Converts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.lot_activity.*
import kotlinx.android.synthetic.main.lot_jd_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class LotJdFragment : BaseFragment(R.layout.lot_jd_fragment) {
    private val vm by viewModels<LotJdViewModel>()

    @Inject
    lateinit var mConfirmDialog: ConfirmDialog

    @Inject
    lateinit var mUnitPopWindow: UnitPopWindow
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lot_jd_single_input_et.addTextChangedListener(mTextWatcher)//监听单式输入框
        //删除重复、错误号码
        lot_jd_remove_repeat_nums_tv.setOnClickListener {
            mTextWatcher.repeatNdErrorNums(
                lot_jd_single_input_et.text.toString().trim()
                    .replace(",", ""), false
            )
        }

        //清空号码
        initclearNums()

        //投注参数
        initAmountUnitPopWin()//单位
        log_jd_money_unit_tv.setOnClickListener {
            mUnitPopWindow.show(log_jd_money_unit_tv)
        }

        //倍数
        lot_jd_multiple_et.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                try {
                    val toLong = s.toString().toLong()
                    if (toLong < 1) {
                        lot_jd_multiple_et.setText("1")
                        lot_jd_multiple_et.setSelection(lot_jd_multiple_et.length())
                    }
                } catch (e: Exception) {
                    lot_jd_multiple_et.setText("1")
                    lot_jd_multiple_et.setSelection(lot_jd_multiple_et.length())
                }
            }
        })

        //下注
        lot_jd_direct_betting_tv.setOnClickListener {
            mTextWatcher.repeatNdErrorNums(
                lot_jd_single_input_et.text.toString().trim().replace(",", ""), false
            )
            vm.verifyNdGenLotParams(
                lot_jd_single_input_et.text.toString().trim(),
                lot_jd_multiple_et.text.toString().trim(),
                mMoneyUnit,
                getSelectedDigit(),
                verifyDigit(mAtLeastDigit),
                mAtLeastDigit,
                mNoteCount,
                toast,
            ) { lotParam: LotParam?, error: (String) -> Unit ->
                aliveActivity<LotActivity>()?.lotByDialog(
                    lotParam,
                    error = error
                )
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        aliveActivity<LotActivity>()?.mPlayLoadingLayout?.setRetryListener {
            vm.getBetType()
        }
        vm.initAmountUnit {
            mMoneyUnit = it
            log_jd_money_unit_tv.text = Converts.unit2String(it)
        }
        vm.initGame()
        vm.getBetType()
    }

    private var mNeedDigit: Boolean? = null
    private var mAtLeastDigit: Int? = null
    override fun observe() {
        vm.mGameBetTypeData.getLiveData().observe(this) {
            vm.renderPlayNdBet { playSelectedPos, betSelectedPos ->
                updatePlayList(betTypeDatas = it, playSelectedPos, betSelectedPos)
            }
        }
        vm.mNeedDigit.getLiveData().observe(this) {
            lot_jd_bet_digit_ll.visible(it)
            mNeedDigit = it
        }
        vm.mIsSingleStyle.getLiveData().observe(this) {
            switchDanFuStyle(it)
        }
        vm.mAtLeastDigit.getLiveData().observe(this) {
            mAtLeastDigit = it
            setAtLeastDigit(mAtLeastDigit)
        }
    }

    //region 单式输入框
    var mSingleNumCount: Int = 5//单注号码数
    var mNoteCount: Int = 0//投注注数
    private val mTextWatcher = object : TextWatcherAdapter() {
        var end = false
        var canPutBasket = true
        var watcher = true
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            end = s?.let { it.length == lot_jd_single_input_et.selectionEnd } ?: false
        }

        override fun afterTextChanged(s: Editable?) {
            if (!watcher) {
                watcher = true
                return
            }
            val oneNote = s?.length ?: 0 >= mSingleNumCount//一注
            if (canPutBasket != oneNote) {
                //输入或清空
                canPutBasket = oneNote
                lot_jd_add_to_shopping_bar_tv.text =
                    getString(if (oneNote) R.string.lot_jd_put_shopping_bar else R.string.lot_jd_shopping_bar)
                lot_jd_add_to_shopping_bar_tv.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (oneNote) 12f else 15f
                )
            }

            if (end) {
                s?.toString()?.replace(",", "")?.let {
                    if (it.length <= mSingleNumCount) return@let
                    repeatNdErrorNums(it)
                }
            }
        }

        fun repeatNdErrorNums(text: String, fromInput: Boolean = true) {
            mNoteCount = 0
            if (text.length <= mSingleNumCount) return
            val buff = StringBuilder(text)
            var offset = mSingleNumCount
            while (offset < buff.length && offset > 0) {
                buff.insert(offset, ",")
                offset += 1 + mSingleNumCount
                mNoteCount++
            }
            val repeatNdErrorNums =
                if (!fromInput && buff.isNotEmpty() && buff.length - buff.lastIndexOf(",") <= mSingleNumCount) {
                    buff.substring(0, buff.lastIndexOf(","))
                } else buff.toString()
            watcher = false
            lot_jd_single_input_et.setText(repeatNdErrorNums)
            lot_jd_single_input_et.setSelection(repeatNdErrorNums?.length ?: 0)
        }
    }
    //endregion

    //region 传递参数gameType、gameId、gameName
    companion object {
        fun newInstance(gameType: Int, gameId: Int, gameName: String?): LotJdFragment {
            val fragment = LotJdFragment()
            val args = Bundle()
            args.putInt(EXTRA.ID_GAME_EXTRA, gameId)
            args.putInt(EXTRA.TYPE_GAME_EXTRA, gameType)
            args.putString(EXTRA.NAME_GAME_EXTRA, gameName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.initExtraVar(arguments)
    }
    //endregion

    //region 金额单位popup
    private var mUnitPattern: String? = null
    private fun setUnitPattern(unitPattern: String) {
        mUnitPattern = unitPattern
        mUnitPopWindow.setPattern(unitPattern)
    }

    private var mMoneyUnit: Int = 1
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
        }
    }
    //endregion

    //region 清空号码
    private fun initclearNums() {
        //清空确认弹窗
        mConfirmDialog.contentText("是否清除已选择号码").onConfirm {
            if (MODE_SINGLE == mMode) {
                //单式
                lot_jd_single_input_et.setText("")

            } else {
                //复式
            }
        }
        lot_jd_input_clear_iv.setOnClickListener {
            mConfirmDialog.show(childFragmentManager)
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

    //region 一级玩法
    private fun updatePlayList(
        betTypeDatas: GameBetTypeData?, playSelectedPos: Int,
        betSelectedPos: Int,
    ) {
        aliveActivity<LotActivity>()?.lotMenuPlayLayer1Rv?.setListOrUpdate(betTypeDatas) {
            LotPlayAdapter(betTypeDatas).apply {
                setOnItemClickListener { _: BaseQuickAdapter<*, *>, _: View, position: Int ->
                    //一级玩法选中
                    notifySelectedPositionWithPayLoads(position)
                    updatePlayLayer2List(betTypeDatas?.get(position), position, betSelectedPos)
                }
            }
        }
        aliveActivity<LotActivity>()?.lotMenuPlayLayer1Rv?.adapter?.let {
            if (it is BaseSelectedQuickAdapter<*, *>) it.notifySelectedPosition(playSelectedPos)
        }
        updatePlayLayer2List(
            if (betTypeDatas.validIndex(playSelectedPos)) betTypeDatas?.get(playSelectedPos) else null,
            playSelectedPos,
            betSelectedPos
        )
    }
    //endregion

    //region 二级玩法、二级玩法组
    private var mPlaySelectedTmpRef = -1
    private fun updatePlayLayer2List(
        betTypeItem: PlayItem?, playSelectedPos: Int,
        betSelectedPos: Int,
    ) {
        mPlaySelectedTmpRef = playSelectedPos
        vm.initBetSelected(betTypeItem) { onBetSelected(it) }
        aliveActivity<LotActivity>()?.lotMenuPlayLayer2Rv?.setListOrUpdate(betTypeItem?.list) {
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
                            object : LotBetAdapter(it) {
                                override fun convert(
                                    holder: BaseViewHolder,
                                    item: BetItem,
                                ) {
                                    super.convert(holder, item)
                                    holder.getView<TextView>(R.id.text_common_tv).isSelected =
                                        isSelected(holder) && vm.isCurPlayNdGroup(
                                            mPlaySelectedTmpRef, groupPosition
                                        )
                                }

                                override fun convert(
                                    holder: BaseViewHolder,
                                    item: BetItem,
                                    payloads: List<Any>,
                                ) {
                                    holder.getView<TextView>(R.id.text_common_tv).isSelected =
                                        isSelected(holder) && vm.isCurPlayNdGroup(
                                            mPlaySelectedTmpRef, groupPosition
                                        )
                                }
                            }.apply {
                                setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View, position: Int ->
                                    //选中玩法：更新一级玩法下标、二级玩法组下标、二级玩法下标
                                    vm.setSelectedPosOnBetSelected(
                                        mPlaySelectedTmpRef,
                                        groupPosition,
                                        position
                                    ) {
                                        aliveActivity<LotActivity>()?.lotMenuPlayLayer2Rv?.adapter?.notifyItemChanged(
                                            it, PAY_LOADS_SELECTED
                                        )
                                    }
                                    notifySelectedPositionWithPayLoads(position, false)
                                    adapter.getItemOrNull(position)?.let {
                                        if (it is BetItem) {
                                            vm.setSelectedBetItem(it)
                                            onBetSelected(it)
                                        }
                                    }
                                    aliveActivity<LotActivity>()?.gonePlayMenu()
                                }
                            }
                        }
                    }

                    if (vm.isCurGroup(groupPosition)) {
                        groupHolder.getView<RecyclerView>(R.id.lot_jd_play_group_rv).adapter?.let {
                            if (it is BaseSelectedQuickAdapter<*, *>) {
                                it.notifySelectedPosition(betSelectedPos)
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

    //更新玩法时：title、玩法配置、投注单位更新
    private fun onBetSelected(item: BetItem?) {
        item?.let {
            aliveActivity<LotActivity>()?.updateMarqueeView(it.getPlayTitle())

            val playId = it.betType//玩法id
            vm.getLocalBetType(playId)//获取玩法配置

            setUnitPattern(it.pattern)//更新投注单位
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.cacheMoneyUnit(mMoneyUnit)
        vm.cachePlay4GameId()
    }

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
        if (lot_jd_bet_digit_wan_cb.isChecked()) {
            sb.append("0").append(",")
        }
        if (lot_jd_bet_digit_qian_cb.isChecked()) {
            sb.append("1").append(",")
        }
        if (lot_jd_bet_digit_bai_cb.isChecked()) {
            sb.append("2").append(",")
        }
        if (lot_jd_bet_digit_shi_cb.isChecked()) {
            sb.append("3").append(",")
        }
        if (lot_jd_bet_digit_ge_cb.isChecked()) {
            sb.append("4").append(",")
        }

        //移除最后一个“,”
        if (sb.length > 1) {
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    private fun verifyDigit(atLeastDigit: Int?): Boolean {
        if (null == atLeastDigit) return true
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
        return checkedCount >= atLeastDigit
    }
    //endregion

    //region 切换单式、复式
    private val MODE_SINGLE = 0//单式
    private val MODE_DUPLEX = 1//复式
    private var mMode = MODE_DUPLEX//模式
    private fun switchDanFuStyle(isSingleStyle: Boolean) {
        mMode = if (isSingleStyle) MODE_SINGLE else MODE_DUPLEX
    }

    fun isSingleStyle(): Boolean {
        return MODE_SINGLE == mMode
    }
    //endregion
}