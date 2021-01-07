package com.bdb.lottery.biz.lot.jd.duplex.adapter

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import androidx.core.util.isNotEmpty
import androidx.core.util.valueIterator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


class LotDuplexAdapter constructor(
    private val gameType: Int,
    private var betTypeId: Int,
    private var isLHH: Boolean = false,
    duplexDatas: MutableList<LotDuplexData>?,
) : BaseQuickAdapter<LotDuplexData, BaseViewHolder>(
    R.layout.lot_duplex_item,
    duplexDatas
) {
    private val PAY_LOAD_LABEL = "PAY_LOAD_LABEL"
    private val PAY_LOAD_BIG = "PAY_LOAD_BIG"
    private val PAY_LOAD_SMALL = "PAY_LOAD_SMALL"
    private val PAY_LOAD_SINGLE = "PAY_LOAD_SINGLE"
    private val PAY_LOAD_DOUBLE = "PAY_LOAD_DOUBLE"
    private val m11x5TuoDan =//11选5组选胆拖的胆码
        betTypeId == 586 || betTypeId == 587 || betTypeId == 179 || betTypeId == 182 || betTypeId == 176
    private val mSpanCount: Int = if (isLHH) {
        if (itemCount > 3) 3 else 2
    } else {
        when (gameType) {
            GAME.TYPE_GAME_11X5 -> 6
            GAME.TYPE_GAME_K3 -> 120
            else -> 5
        }
    }
    private val mSubAdapters: SparseArray<LotDuplexSubAdapter> = SparseArray()
    override fun convert(holder: BaseViewHolder, item: LotDuplexData) {
        val adapterPosition = holder.adapterPosition
        //label
        holder.setColorStateList(
            context,
            R.id.lot_duplex_item_label_tv,
            R.color.lot_duplex_item_label_text_color_selector
        )
        holder.setBackgroundResource(
            R.id.lot_duplex_item_label_tv,
            when (gameType) {
                GAME.TYPE_GAME_11X5 -> R.drawable.lot_duplex_item_label_bg_11x5_selector
                GAME.TYPE_GAME_PK8, GAME.TYPE_GAME_PK10 -> R.drawable.lot_duplex_item_label_bg_pk_selector
                else -> R.drawable.lot_duplex_item_label_bg_selector
            }
        )
        val label = item.label
        val gone = gameType == GAME.TYPE_GAME_K3 || isLHH
        holder.setGone(//龙虎、快三不显示label
            R.id.lot_duplex_item_label_tv,
            gone
        )
        if (!gone) {
            if (GAME.TYPE_GAME_11X5 == gameType) {
                if (GAME.TYPE_GAME_11X5 == gameType) holder.getView<TextView>(R.id.lot_duplex_item_label_tv)
                    .setEms(1)
                holder.setWH(
                    R.id.lot_duplex_item_label_tv, 24f,
                    label?.let { if (it.length > 3) 58f else if (it.length > 2) 48f else 38f }
                        ?: 38f)
            } else {
                holder.setW(
                    R.id.lot_duplex_item_label_tv,
                    label?.let { if (it.length > 3) 68f else if (it.length > 2) 48f else 38f }
                        ?: 38f)
            }
        }

        holder.setText(R.id.lot_duplex_item_label_tv, label)

        //大小单双
        val dxdsVisible = item.dxdsVisible
        if (dxdsVisible && 0 == adapterPosition && m11x5TuoDan) {
            //11选5组选胆拖的胆码不需要显示大小单双
            holder.setVisible(R.id.lot_duplex_item_dxds_rl, false)
        } else {
            holder.setGone(R.id.lot_duplex_item_dxds_rl, !dxdsVisible)
        }
        if (GAME.TYPE_GAME_PK8 == gameType || GAME.TYPE_GAME_PK10 == gameType) {
            holder.setBackgroundResource(
                R.id.lot_duplex_item_big,
                R.drawable.lot_duplex_sub_item_pk_dxds_selector
            )
            holder.setBackgroundResource(
                R.id.lot_duplex_item_small,
                R.drawable.lot_duplex_sub_item_pk_dxds_selector
            )
            holder.setBackgroundResource(
                R.id.lot_duplex_item_single,
                R.drawable.lot_duplex_sub_item_pk_dxds_selector
            )
            holder.setBackgroundResource(
                R.id.lot_duplex_item_double,
                R.drawable.lot_duplex_sub_item_pk_dxds_selector
            )
        }
        //球
        val rv: RecyclerView = holder.getView(R.id.lot_duplex_item_rv)
        rv.layoutManager = GridLayoutManager(context, mSpanCount)
        rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                var top = 0
                parent.layoutManager?.let {
                    if (it is GridLayoutManager) {
                        val spanCount = it.spanCount
                        val position = parent.getChildAdapterPosition(view)
                        if (spanCount > 0 && position / spanCount > 0) {
                            top = Sizes.dp2px(6f)
                        }
                    }
                }
                outRect.set(0, top, 0, 0)
            }
        })
        rv.adapter?.let {
            if (it is LotDuplexSubAdapter) it.notifyChange(
                betTypeId,
                mSpanCount,
                false,
                item
            )
        } ?: let {
            rv.adapter = LotDuplexSubAdapter(
                m11x5TuoDan && adapterPosition == 0,//11选5胆码：单选
                gameType,
                betTypeId,
                mSpanCount,
                false,
                item
            ) {
                //号码球选中切换回调
                holder.setItemChildSelected(R.id.lot_duplex_item_label_tv, !it.isNullOrEmpty())
                if (m11x5TuoDan) {
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

    override fun convert(holder: BaseViewHolder, item: LotDuplexData, payloads: List<Any>) {
        val position = holder.adapterPosition
        when (payloads[0]) {
            PAY_LOAD_BIG -> holder.setItemChildSelected(
                R.id.lot_duplex_item_big,
                position == mBigSelectedPos
            )
            PAY_LOAD_SMALL -> holder.setItemChildSelected(
                R.id.lot_duplex_item_small,
                position == mSmallSelectedPos
            )
            PAY_LOAD_SINGLE -> holder.setItemChildSelected(
                R.id.lot_duplex_item_single,
                position == mSingleSelectedPos
            )
            PAY_LOAD_DOUBLE -> holder.setItemChildSelected(
                R.id.lot_duplex_item_double,
                position == mDoubleSelectedPos
            )
            PAY_LOAD_LABEL -> holder.setItemChildSelected(
                R.id.lot_duplex_item_big,
                position == mBigSelectedPos
            )
            else -> {
                holder.setItemChildSelected(
                    R.id.lot_duplex_item_big,
                    position == mBigSelectedPos
                )
            }
        }
    }

    //region 大小单双单击事件
    private var mBigSelectedPos = -1
    private var mSmallSelectedPos = -1
    private var mSingleSelectedPos = -1
    private var mDoubleSelectedPos = -1

    init {
        addChildClickViewIds(
            R.id.lot_duplex_item_big,
            R.id.lot_duplex_item_small,
            R.id.lot_duplex_item_single,
            R.id.lot_duplex_item_double
        )
        setOnItemChildClickListener { _: BaseQuickAdapter<Any?, BaseViewHolder>, view: View, position: Int ->
            if (position < mSubAdapters.size()) {
                mSubAdapters[position]?.let {
                    when (view.id) {
                        R.id.lot_duplex_item_big -> {
                            it.selectedBig()
                            val prePos = mBigSelectedPos
                            mBigSelectedPos = position
                            if (data.validIndex(prePos)) notifyItemChanged(prePos, PAY_LOAD_BIG)
                            notifyItemChanged(position, PAY_LOAD_BIG)
                        }
                        R.id.lot_duplex_item_small -> {
                            it.selectedSmall()
                            val prePos = mSmallSelectedPos
                            mSmallSelectedPos = position
                            if (data.validIndex(prePos)) notifyItemChanged(prePos, PAY_LOAD_SMALL)
                            notifyItemChanged(position, PAY_LOAD_SMALL)
                        }
                        R.id.lot_duplex_item_single -> {
                            it.selectedSingle()
                            val prePos = mSingleSelectedPos
                            mSingleSelectedPos = position
                            if (data.validIndex(prePos)) notifyItemChanged(prePos, PAY_LOAD_SINGLE)
                            notifyItemChanged(position, PAY_LOAD_SINGLE)
                        }
                        R.id.lot_duplex_item_double -> {
                            it.selectedDouble()
                            val prePos = mDoubleSelectedPos
                            mDoubleSelectedPos = position
                            if (data.validIndex(prePos)) notifyItemChanged(prePos, PAY_LOAD_DOUBLE)
                            notifyItemChanged(position, PAY_LOAD_DOUBLE)
                        }
                    }
                }
            }
        }
    }
    //endregion

    //冷热
    fun hotVisible(visible: Boolean) {
        if (!mSubAdapters.isNotEmpty()) {
            for (adapter in mSubAdapters.valueIterator()) {
                adapter.hotVisible(visible)
            }
        }
    }

    //遗漏
    fun leaveVisible(visible: Boolean) {
        if (!mSubAdapters.isNotEmpty()) {
            for (adapter in mSubAdapters.valueIterator()) {
                adapter.hotVisible(visible)
            }
        }
    }

    fun notifyChange(
        betTypeId: Int, isLHH: Boolean,//龙虎和
        duplexDatas: MutableList<LotDuplexData>?,
    ) {
        this.betTypeId = betTypeId
        this.isLHH = isLHH
        setNewInstance(duplexDatas)
    }
}