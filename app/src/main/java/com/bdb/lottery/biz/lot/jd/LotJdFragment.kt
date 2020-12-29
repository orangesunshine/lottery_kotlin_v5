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
import com.bdb.lottery.utils.cache.TCache
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

    private val MODE_SINGLE = 0//单式
    private val MODE_DUPLEX = 1//复式
    private var mMode = MODE_DUPLEX//模式
    private var mAmountUnit = 1//默认元
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lot_jd_single_input_et.addTextChangedListener(mTextWatcher)//监听单式输入框
        //删除重复、错误号码
        lot_jd_remove_repeat_nums_tv.setOnClickListener {
            mTextWatcher.repeatNdErrorNums(
                lot_jd_single_input_et.text.toString().trim()
                    .replace(",", ""),
                singleNumCount
            )
        }

        //清空号码
        clearNums()

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
            val multiple = lot_jd_multiple_et.text.toString().trim()
            val nums = if (mIsSingleStyle) lot_jd_single_input_et.text.toString().trim() else null
            aliveActivity<LotActivity>()?.lotByDialog(
                vm.mToken, nums, multiple, mAmountUnit, null
            ) {
                vm.mToken = it
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.initGame(mGameId.toString())
        vm.getBetType(mGameId.toString())
    }

    override fun observe() {
        vm.mGameInitData.getLiveData().observe(this) {}
        vm.mGameBetTypeData.getLiveData().observe(this) { updatePlayMenu(it) }
        vm.subPlayMethod.getLiveData()
            .observe(this, { switchDanFuStyle(it?.subPlayMethodDesc?.isdanshi ?: true) })//玩法相关
    }

    //切换单式、复式
    private var mIsSingleStyle: Boolean = false
    private fun switchDanFuStyle(isSingleStyle: Boolean) {
        mMode = if (isSingleStyle) MODE_SINGLE else MODE_DUPLEX
    }

    //玩法菜单
    private fun updatePlayMenu(betTypeData: GameBetTypeData?) {
        if (!betTypeData.isNullOrEmpty()) {
            updatePlayLayer1List(betTypeData)
        }
    }

    //region 单式输入框
    private var singleNumCount: Int = 5//单注号码数
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
            val oneNote = s?.length ?: 0 >= singleNumCount//一注
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
                    if (it.length <= singleNumCount) return@let
                    repeatNdErrorNums(it, singleNumCount)
                }
            }
        }

        //删除错误重复号码
        fun repeatNdErrorNums(text: String, count: Int) {
            if (text.length <= count) return
            val buff = StringBuilder(text)
            var offset = count
            while (offset < buff.length && offset > 0) {
                buff.insert(offset, ",")
                offset += 1 + count
            }
            watcher = false
            val ret = buff.toString()
            lot_jd_single_input_et.setText(ret)
            lot_jd_single_input_et.setSelection(ret.length)
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

    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mGameId = it.getInt(EXTRA.ID_GAME_EXTRA)
            mGameType = it.getInt(EXTRA.TYPE_GAME_EXTRA)
            mGameName = it.getString(EXTRA.NAME_GAME_EXTRA)
        }
        playMenuParamsCache(mGameId)
    }
    //endregion

    //region 金额单位popup
    private var mUnitPattern: String? = null
    fun setUnitPattern(unitPattern: String) {
        mUnitPattern = unitPattern
        mUnitPopWindow.setPattern(unitPattern)
    }

    private fun initAmountUnitPopWin() {
        mUnitPopWindow.init { view: View ->
            mUnitPopWindow.dismiss()
            when (view.id) {
                R.id.lot_jd_money_unit_yuan_tv -> mAmountUnit = 1
                R.id.lot_jd_money_unit_jiao_tv -> mAmountUnit = 2
                R.id.lot_jd_money_unit_fen_tv -> mAmountUnit = 3
                R.id.lot_jd_money_unit_li_tv -> mAmountUnit = 4
            }
            log_jd_money_unit_tv.text = Converts.unit2String(mAmountUnit)
        }
    }
    //endregion

    //region 清空号码
    private fun clearNums() {
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
    fun updateStatus(closed: Boolean) {
        if (mClosed == closed) return
        val can = !closed//能否下注
        lot_jd_direct_betting_tv.text =
            getString(if (can) R.string.lot_jd_direct_betting else R.string.lot_jd_closed)
        lot_jd_direct_betting_tv.isEnabled = can
        mClosed = closed
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
    private fun updatePlayLayer1List(betTypeDatas: GameBetTypeData?) {
        aliveActivity<LotActivity>()?.lotMenuPlayLayer1Rv?.setListOrUpdate(betTypeDatas) {
            LotPlayAdapter(betTypeDatas).apply {
                setOnItemClickListener { _: BaseQuickAdapter<*, *>, _: View, position: Int ->
                    //一级玩法选中
                    notifySelectedPositionWithPayLoads(position)
                    updatePlayLayer2List(position, betTypeDatas?.get(position))
                }
            }
        }
        aliveActivity<LotActivity>()?.lotMenuPlayLayer1Rv?.adapter?.let {
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
        updatePlaySelected(betTypeItem)
        mPlayLayer1Tmp = playLayer1
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
                                        isSelected(holder) && mPlayGroup == groupPosition && mPlayLayer1Tmp == mPlayLayer1
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
                                setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View, position: Int ->
                                    //选中玩法：更新一级玩法下标、二级玩法组下标、二级玩法下标
                                    if (mPlayLayer1 == mPlayLayer1Tmp && mPlayGroup == groupPosition && mPlayLayer2 == position) return@setOnItemClickListener
                                    if (mPlayLayer1 != mPlayLayer1Tmp) {
                                        mPlayLayer1 = mPlayLayer1Tmp
                                    } else {
                                        if (mPlayGroup != groupPosition) {
                                            val preGroupPosition = mPlayGroup
                                            aliveActivity<LotActivity>()?.lotMenuPlayLayer2Rv?.adapter?.notifyItemChanged(
                                                preGroupPosition, PAY_LOADS_SELECTED
                                            )
                                        }
                                    }
                                    notifySelectedPositionWithPayLoads(position)
                                    mPlayGroup = groupPosition
                                    mPlayLayer2 = position
                                    adapter.getItemOrNull(position)?.let {
                                        if (it is PlayLayer2Item) {
                                            aliveActivity<LotActivity>()?.updateMarqueeView(it.getPlayTitle())
                                            //玩法id
                                            val playId = it.betType
                                            vm.getLotType(playId)
                                        }
                                    }
                                    aliveActivity<LotActivity>()?.gonePlayMenu()
                                }
                            }
                        }
                    }

                    if (groupPosition == mPlayGroup)
                        groupHolder.getView<RecyclerView>(R.id.lot_jd_play_group_rv).adapter?.let {
                            if (it is BaseSelectedQuickAdapter<*, *>) {
                                it.notifySelectedPosition(mPlayLayer2)
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

    private fun updatePlaySelected(item: PlayLayer1Item?) {
        item?.list?.let {
            if (mPlayGroup < it.size) {
                it.get(mPlayGroup).list?.let {
                    if (mPlayLayer2 < it.size) {
                        val playItem = it.get(mPlayLayer2)
                        aliveActivity<LotActivity>()?.updateMarqueeView(playItem.getPlayTitle())
                        setUnitPattern(playItem.pattern)
                    }
                }
            }
        }
    }

    @Inject
    lateinit var tCache: TCache
    override fun onDestroyView() {
        super.onDestroyView()
        tCache.cachePlay4GameId(mGameId, mPlayLayer1, mPlayGroup, mPlayLayer2, mPlayId)
    }
}