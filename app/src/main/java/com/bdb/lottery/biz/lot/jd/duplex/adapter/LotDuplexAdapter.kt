package com.bdb.lottery.biz.lot.jd.duplex.adapter

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import androidx.core.util.isNotEmpty
import androidx.core.util.set
import androidx.core.util.valueIterator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexLd
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.lot.Lots
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


class LotDuplexAdapter constructor(
    private val gameType: Int,
    private var betTypeId: Int,
    private var ballTextList: List<String>?,//不为空：非数字球，否则数字球
    duplexDatas: MutableList<LotDuplexData>?,
    private val noteCountBlock: ((List<List<String?>?>) -> Unit)?,
) : BaseQuickAdapter<LotDuplexData, BaseViewHolder>(
    R.layout.lot_duplex_item,
    duplexDatas
) {
    private val PAY_LOAD_BIG = "PAY_LOAD_BIG"
    private val PAY_LOAD_SMALL = "PAY_LOAD_SMALL"
    private val PAY_LOAD_SINGLE = "PAY_LOAD_SINGLE"
    private val PAY_LOAD_DOUBLE = "PAY_LOAD_DOUBLE"
    private val PAY_LOAD_NONE = "PAY_LOAD_NONE"//取消大小单双选择
    private var m11x5DanTuo =//11选5组选胆码和拖码
        betTypeId == 586 || betTypeId == 587 || betTypeId == 179 || betTypeId == 182 || betTypeId == 176
    private var mSpanCount: Int = spanCountByGameTypeNdLHH(gameType)

    //region 根据彩种id，生成每行数量、或权重（12：快三特殊处理：数字球一行6列，非数字球：一行4列）
    private fun spanCountByGameTypeNdLHH(gameType: Int): Int {
        return if (!ballTextList.isNullOrEmpty() && Lots.isLHH(ballTextList!![0])) {
            if (ballTextList!!.size > 2) 3 else 2
        } else when (gameType) {
            GAME.TYPE_GAME_11X5 -> 6
            GAME.TYPE_GAME_K3 -> 12
            else -> 5
        }
    }
    //endregion

    private val mSubAdapters: SparseArray<LotDuplexSubAdapter> = SparseArray()
    override fun convert(holder: BaseViewHolder, item: LotDuplexData) {
        val adapterPosition = holder.adapterPosition
        mDxdsSparseArray.put(adapterPosition, -1)
        //label(万千百十个)
        renderLabel(item.label, holder)
        //球
        renderBall(holder, item, adapterPosition)
        //大小单双
        renderDxds(item, adapterPosition, holder)
        //divide
        holder.setGone(R.id.lot_duplex_item_divide, adapterPosition == itemCount - 1)
    }

    //region 每项对应的：号码球、大小单双、万千百十个
    private fun renderBall(
        holder: BaseViewHolder,
        item: LotDuplexData,
        adapterPosition: Int,
    ) {
        val rv: RecyclerView = holder.getView(R.id.lot_duplex_item_rv)
        //球：网格布局
        rv.layoutManager = GridLayoutManager(context, mSpanCount).apply {
            if (gameType == GAME.TYPE_GAME_K3) {
                this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        //快三特殊处理：数字球一行6列，非数字球：一行4列
                        return if (item.ballTextList?.get(position).isDigit()) 2 else 3
                    }
                }
            }
        }
        rv.adapter?.let {
            //刷新item同时刷新号码球
            if (it is LotDuplexSubAdapter) it.notifyChangeWhenPlayChange(
                m11x5DanTuo && adapterPosition == 0,//11选5胆码：单选
                betTypeId,
                mSpanCount,
                item
            )
        } ?: let {//初始化号码球
            rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) {
                    var top = 0
                    parent.layoutManager?.let {
                        if (it is GridLayoutManager) {
                            val spanCount = it.spanCount
                            val position = parent.getChildAdapterPosition(view)
                            //行间距6dp：第二行开始topMargin；快三和值暂无法求出position对应行数，第一行非数字球4列，第5球开始topMargin
                            if (spanCount > 0 && (position / spanCount > 0 || (Lots.isK3HeZhi(
                                    betTypeId
                                ) && position > 3))
                            ) {
                                top = Sizes.dp2px(6f)
                            }
                        }
                    }
                    outRect.set(0, top, 0, 0)
                }
            })
            rv.adapter = LotDuplexSubAdapter(
                m11x5DanTuo && adapterPosition == 0,//11选5胆码：单选
                gameType,
                betTypeId,
                mSpanCount,
                item
            ) {
                //号码球选中切换回调
                noteCountBlock?.invoke(getAllSelectedNums())
                holder.setItemChildSelected(R.id.lot_duplex_item_label_tv, !it.isNullOrEmpty())
                val pre = mDxdsSparseArray[adapterPosition]
                val tag =
                    mSubAdapters[adapterPosition]?.selectedList2DxdsTag(it)//如果号码球手动选择满足大小单双规则，联动大小单双选中
                if (tag != pre) {
                    when (tag) {
                        0 -> notifyItemChanged(adapterPosition, PAY_LOAD_BIG)
                        1 -> notifyItemChanged(adapterPosition, PAY_LOAD_SMALL)
                        2 -> notifyItemChanged(adapterPosition, PAY_LOAD_SINGLE)
                        3 -> notifyItemChanged(adapterPosition, PAY_LOAD_DOUBLE)
                        else -> notifyItemChanged(adapterPosition, PAY_LOAD_NONE)
                    }
                }
                if (m11x5DanTuo) {
                    //11选5胆码、拖码处理：不能同时选中同一个号码球
                    val selectedPositions = mSubAdapters[0].getSelectedPositions()//胆码选中下标
                    if (!selectedPositions.isNullOrEmpty()) {
                        if (adapterPosition == 0) {
                            //胆码
                            if (!it.isNullOrEmpty() && it.contains(selectedPositions[0])) {
                                mSubAdapters[1]?.notifySingleUnSelectedPositionWithPayLoads(
                                    selectedPositions[0]
                                )
                            }
                        } else if (adapterPosition == 1) {
                            //拖码
                            if (!selectedPositions.isNullOrEmpty() && it.contains(selectedPositions[0])) {
                                mSubAdapters[0]?.notifySingleUnSelectedPositionWithPayLoads(
                                    selectedPositions[0]
                                )
                            }
                        }
                    }
                }
            }.apply { mSubAdapters.put(adapterPosition, this) }
        }
    }

    private fun renderDxds(
        item: LotDuplexData,
        adapterPosition: Int,
        holder: BaseViewHolder,
    ) {
        val dxdsVisible = item.dxdsVisible//数据库is_show_type_select状态
        if (m11x5DanTuo && 0 == adapterPosition) {
            //11选5胆码单选，没有大小单双
            holder.setVisible(R.id.lot_duplex_item_dxds_rl, false)
        } else {
            holder.setGone(R.id.lot_duplex_item_dxds_rl, !dxdsVisible)
        }
        if (dxdsVisible) {
            //pk10换肤：大小单双选中背景、文字颜色
            if (GAME.TYPE_GAME_PK8 == gameType || GAME.TYPE_GAME_PK10 == gameType) {
                holder.setBackgroundResource(
                    R.id.lot_duplex_item_big,
                    R.drawable.lot_duplex_sub_item_pk_dxds_selector
                )
                holder.setTextColorStateList(
                    context,
                    R.id.lot_duplex_item_big,
                    R.color.lot_duplex_sub_item_dxds_text_color_selector
                )
                holder.setBackgroundResource(
                    R.id.lot_duplex_item_small,
                    R.drawable.lot_duplex_sub_item_pk_dxds_selector
                )
                holder.setTextColorStateList(
                    context,
                    R.id.lot_duplex_item_small,
                    R.color.lot_duplex_sub_item_dxds_text_color_selector
                )
                holder.setBackgroundResource(
                    R.id.lot_duplex_item_single,
                    R.drawable.lot_duplex_sub_item_pk_dxds_selector
                )
                holder.setTextColorStateList(
                    context,
                    R.id.lot_duplex_item_single,
                    R.color.lot_duplex_sub_item_dxds_text_color_selector
                )
                holder.setBackgroundResource(
                    R.id.lot_duplex_item_double,
                    R.drawable.lot_duplex_sub_item_pk_dxds_selector
                )
                holder.setTextColorStateList(
                    context,
                    R.id.lot_duplex_item_double,
                    R.color.lot_duplex_sub_item_dxds_text_color_selector
                )
            }
        }
    }

    private fun renderLabel(
        label: String?,
        holder: BaseViewHolder,
    ) {
        val gone = gameType == GAME.TYPE_GAME_K3 || !ballTextList.isNullOrEmpty()
        holder.setGone(//龙虎、快三不显示label
            R.id.lot_duplex_item_label_tv,
            gone
        )
        if (!gone) {
            //选中文字颜色
            holder.setTextColorStateList(
                context,
                R.id.lot_duplex_item_label_tv,
                R.color.lot_duplex_item_label_text_color_selector
            )
            //选中背景
            holder.setBackgroundResource(
                R.id.lot_duplex_item_label_tv,
                when (gameType) {
                    GAME.TYPE_GAME_11X5 -> R.drawable.lot_duplex_item_label_bg_11x5_selector
                    GAME.TYPE_GAME_PK8, GAME.TYPE_GAME_PK10 -> R.drawable.lot_duplex_item_label_bg_pk_selector
                    else -> R.drawable.lot_duplex_item_label_bg_selector
                }
            )
            if (GAME.TYPE_GAME_11X5 == gameType) {
                //11选5：一行6列，label文字竖排：根据文字个数调整高度
                if (GAME.TYPE_GAME_11X5 == gameType) holder.getView<TextView>(R.id.lot_duplex_item_label_tv)
                    .setEms(1)
                holder.setUnitedWH(
                    R.id.lot_duplex_item_label_tv, 24f,
                    label?.let { if (it.length > 3) 58f else if (it.length > 2) 48f else 38f }
                        ?: 38f)
            } else {
                //其他横排：根据文字个数调整label宽度
                holder.setWidthWithUnit(
                    R.id.lot_duplex_item_label_tv,
                    label?.let { if (it.length > 3) 68f else if (it.length > 2) 48f else 38f }
                        ?: 38f)
            }
        }

        holder.setText(R.id.lot_duplex_item_label_tv, label)
    }
    //endregion

    override fun convert(holder: BaseViewHolder, item: LotDuplexData, payloads: List<Any>) {
        val adapterPosition = holder.adapterPosition
        var dxdsTag = -1
        val preTag = mDxdsSparseArray[adapterPosition]
        when (payloads[0]) {
            PAY_LOAD_BIG -> {
                dxdsTag = 0//大
            }
            PAY_LOAD_SMALL -> {
                dxdsTag = 1//小
            }
            PAY_LOAD_SINGLE -> {
                dxdsTag = 2//单
            }
            PAY_LOAD_DOUBLE -> {
                dxdsTag = 3//双
            }
            PAY_LOAD_NONE -> {
                dxdsTag = -1//取消大小单双选中
            }
        }
        if (-1 != preTag)
            dxdsSelectedChange(holder, preTag, false)
        if (dxdsTag == preTag) {
            if (-1 != preTag)
                mDxdsSparseArray[adapterPosition] = -1
        } else {
            dxdsSelectedChange(holder, dxdsTag, true)
            mDxdsSparseArray[adapterPosition] = dxdsTag
        }
    }

    //region tag对应大小大双按钮，切换select
    private fun dxdsSelectedChange(holder: BaseViewHolder, tag: Int, selected: Boolean) {
        when (tag) {
            0 -> holder.setItemChildSelected(
                R.id.lot_duplex_item_big,
                selected
            )
            1 -> holder.setItemChildSelected(
                R.id.lot_duplex_item_small,
                selected
            )
            2 -> holder.setItemChildSelected(
                R.id.lot_duplex_item_single,
                selected
            )
            3 -> holder.setItemChildSelected(
                R.id.lot_duplex_item_double,
                selected
            )
        }
    }
    //endregion

    //region 大小单双单击事件
    private val mDxdsSparseArray: SparseArray<Int> = SparseArray()//0大，1小，2单，3双

    init {
        addChildClickViewIds(
            R.id.lot_duplex_item_big,
            R.id.lot_duplex_item_small,
            R.id.lot_duplex_item_single,
            R.id.lot_duplex_item_double
        )
        setOnItemChildClickListener { _: BaseQuickAdapter<*, *>, view: View, position: Int ->
            if (position < mSubAdapters.size()) {
                mSubAdapters[position]?.let {
                    when (view.id) {
                        R.id.lot_duplex_item_big -> it.bigSelectedChange(mDxdsSparseArray[position] != 0)
                        R.id.lot_duplex_item_small -> it.smallSelectedChange(mDxdsSparseArray[position] != 1)
                        R.id.lot_duplex_item_single -> it.singleSelectedChange(mDxdsSparseArray[position] != 2)
                        R.id.lot_duplex_item_double -> it.doubleSelectedChange(mDxdsSparseArray[position] != 3)
                    }
                }
            }
        }
    }
    //endregion

    //region 冷热
    fun hotVisible(visible: Boolean) {
        if (mSubAdapters.isNotEmpty()) {
            for (adapter in mSubAdapters.valueIterator()) {
                adapter.hotVisible(visible)
            }
        }
    }
    //endregion

    //region 遗漏
    fun leaveVisible(visible: Boolean) {
        if (mSubAdapters.isNotEmpty()) {
            for (adapter in mSubAdapters.valueIterator()) {
                adapter.hotVisible(visible)
            }
        }
    }
    //endregion

    //region 玩法改变刷新
    fun notifyChangeWhenPlayChange(lotDuplexLd: LotDuplexLd) {
        this.betTypeId = lotDuplexLd.betTypeId
        this.ballTextList = lotDuplexLd.ballTextList
        m11x5DanTuo =//11选5组选胆拖的胆码
            betTypeId == 586 || betTypeId == 587 || betTypeId == 179 || betTypeId == 182 || betTypeId == 176
        mSpanCount = spanCountByGameTypeNdLHH(lotDuplexLd.gameType)
        setNewInstance(lotDuplexLd.lotDuplexDatas)
    }
    //endregion


    //region 清空选中号码球
    fun clearSelectedNums() {
        if (mSubAdapters.isNotEmpty()) {
            for (adapter in mSubAdapters.valueIterator()) {
                adapter.notifyUnSelectedAllWithPayLoads()
            }
        }
    }
    //endregion

    fun getAllSelectedNums(): List<List<String?>?> {
        val allSelectedNums = ArrayList<List<String>>()
        if (mSubAdapters.isNotEmpty()) {
            for (adapter in mSubAdapters.valueIterator()) {
                allSelectedNums.add(adapter.getSelectedNums())
            }
        }
        return allSelectedNums
    }
}