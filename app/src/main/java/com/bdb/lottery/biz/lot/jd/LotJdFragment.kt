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
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayGroupItem
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer1Item
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer2Item
import com.bdb.lottery.dialog.ConfirmDialog
import com.bdb.lottery.extension.setListOrUpdate
import com.bdb.lottery.extension.validIndex
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
                    .replace(",", "")
            )
        }

        //清空号码
        InitclearNums()

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
            val subPlayMethod = vm.subPlayMethod.getLiveData().value
            if (null == subPlayMethod) {
                vm.getBetType()
                toast.showWarning("正在获取玩法配置")
                return@setOnClickListener
            }
            //验证digit，选中位置个数

            //验证注数大于0
            vm.repeatNdErrorNums(
                lot_jd_single_input_et.text.toString().trim()
                    .replace(",", "")
            )
            if (vm.mNoteCount < 1) {
                toast.showWarning("请按玩法规则进行投注")
                return@setOnClickListener
            }
            val nums =
                if (MODE_SINGLE == mMode) lot_jd_single_input_et.text.toString().trim() else null

            val multiple = lot_jd_multiple_et.text.toString().trim()//倍数

            aliveActivity<LotActivity>()?.lotByDialog(
                vm.mToken, nums, multiple, vm.mAmountUnit, null
            ) {
                vm.mToken = it//失败刷新token
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        aliveActivity<LotActivity>()?.mPlayLoadingLayout?.setRetryListener {
            vm.getBetType()
        }
        vm.initGame()
        vm.getBetType()
    }

    private var mUserBonus: Double? = 0.0
    override fun observe() {
        vm.mGameInitData.getLiveData().observe(this) {
            mUserBonus = it?.userBonus
        }
        vm.mGameBetTypeData.getLiveData().observe(this) {
            vm.renderPlayNdBet { playSelectedPos, betSelectedPos ->
                updatePlayList(betTypeDatas = it, playSelectedPos, betSelectedPos)
            }
        }
        vm.subPlayMethod.getLiveData()
            .observe(this, {
                switchDanFuStyle(it?.subPlayMethodDesc?.isdanshi ?: true)
            })//玩法相关
    }

    //切换单式、复式
    private val MODE_SINGLE = 0//单式
    private val MODE_DUPLEX = 1//复式
    private var mMode = MODE_DUPLEX//模式
    private fun switchDanFuStyle(isSingleStyle: Boolean) {
        mMode = if (isSingleStyle) MODE_SINGLE else MODE_DUPLEX
    }

    //region 单式输入框
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
            val oneNote = s?.length ?: 0 >= vm.mSingleNumCount//一注
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
                    if (it.length <= vm.mSingleNumCount) return@let
                    vm.repeatNdErrorNums(it)
                }
            }
        }

        //删除错误重复号码
        fun repeatNdErrorNums(text: String) {
            val repeatNdErrorNums = vm.repeatNdErrorNums(text)
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

    private fun initAmountUnitPopWin() {
        mUnitPopWindow.init { view: View ->
            mUnitPopWindow.dismiss()
            var amountUnit = 1
            when (view.id) {
                R.id.lot_jd_money_unit_yuan_tv -> amountUnit = 1
                R.id.lot_jd_money_unit_jiao_tv -> amountUnit = 2
                R.id.lot_jd_money_unit_fen_tv -> amountUnit = 3
                R.id.lot_jd_money_unit_li_tv -> amountUnit = 4
            }
            vm.setAmountUnit(amountUnit)
            log_jd_money_unit_tv.text = Converts.unit2String(amountUnit)
        }
    }
    //endregion

    //region 清空号码
    private fun InitclearNums() {
        //清空确认弹窗
        mConfirmDialog.contentText("是否清除已选择号码").onConfirm {
            if (mMode == MODE_SINGLE) {
                //单式
                lot_jd_single_input_et.setText("")

            } else if (mMode == MODE_DUPLEX) {
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
        betSelectedPos: Int
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
    private var mPlayConfig: PlayLayer2Item? = null
    private var mPlaySelectedTmpRef = -1
    private fun updatePlayLayer2List(
        betTypeItem: PlayLayer1Item?, playSelectedPos: Int,
        betSelectedPos: Int
    ) {
        mPlaySelectedTmpRef = playSelectedPos
        vm.onBetSelected(betTypeItem) { onBetSelected(it) }
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
                                    item: PlayLayer2Item,
                                ) {
                                    super.convert(holder, item)
                                    holder.getView<TextView>(R.id.text_common_tv).isSelected =
                                        isSelected(holder) && vm.isCurPlayNdGroup(
                                            mPlaySelectedTmpRef, groupPosition
                                        )
                                }

                                override fun convert(
                                    holder: BaseViewHolder,
                                    item: PlayLayer2Item,
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
                                    vm.onBetSelectedByClick(
                                        mPlaySelectedTmpRef,
                                        groupPosition,
                                        position
                                    ) {
                                        aliveActivity<LotActivity>()?.lotMenuPlayLayer2Rv?.adapter?.notifyItemChanged(
                                            it, PAY_LOADS_SELECTED
                                        )
                                    }
                                    notifySelectedPositionWithPayLoads(position)
                                    adapter.getItemOrNull(position)?.let {
                                        if (it is PlayLayer2Item) {
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
    private fun onBetSelected(item: PlayLayer2Item?) {
        mPlayConfig = item
        item?.let {
            aliveActivity<LotActivity>()?.updateMarqueeView(it.getPlayTitle())

            val playId = it.betType//玩法id
            vm.getLocalBetType(playId)//获取玩法配置

            setUnitPattern(it.pattern)//更新投注单位
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.cachePlay4GameId()
    }
}