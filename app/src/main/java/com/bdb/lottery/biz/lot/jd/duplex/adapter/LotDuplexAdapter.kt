package com.bdb.lottery.biz.lot.jd.duplex.adapter

import android.util.SparseArray
import android.view.View
import androidx.core.util.isNotEmpty
import androidx.core.util.valueIterator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.setColorStateList
import com.bdb.lottery.extension.setItemChildSelected
import com.bdb.lottery.extension.validIndex
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class LotDuplexAdapter constructor(
    private val gameType: Int,
    private var betTypeId: Int,
    private val balTextList: List<String>?,//龙虎和
    duplexDatas: List<LotDuplexData>?,
) : BaseQuickAdapter<LotDuplexData, BaseViewHolder>(
    R.layout.lot_duplex_item,
    duplexDatas?.toMutableList()
) {
    private val PAY_LOAD_LABEL = "PAY_LOAD_LABEL"
    private val PAY_LOAD_BIG = "PAY_LOAD_BIG"
    private val PAY_LOAD_SMALL = "PAY_LOAD_SMALL"
    private val PAY_LOAD_SINGLE = "PAY_LOAD_SINGLE"
    private val PAY_LOAD_DOUBLE = "PAY_LOAD_DOUBLE"
    private val mSpanCount: Int = if (!balTextList.isNullOrEmpty()) {
        if (balTextList.size > 3) 3 else 2
    } else {
        when (gameType) {
            GAME.TYPE_GAME_11X5 -> 6
            GAME.TYPE_GAME_K3 -> 120
            else -> 5
        }
    }
    private val mSubAdapters: SparseArray<LotDuplexSubAdapter> = SparseArray()
    override fun convert(holder: BaseViewHolder, item: LotDuplexData) {
        val position = holder.adapterPosition
        //label
        holder.setGone(//龙虎、快三不显示label
            R.id.lot_duplex_item_label,
            gameType == GAME.TYPE_GAME_K3 || balTextList.isNullOrEmpty()
        )
        holder.setColorStateList(
            context,
            R.id.lot_duplex_item_label,
            R.color.lot_duplex_item_label_text_color_selector
        )
        holder.setBackgroundResource(
            R.id.lot_duplex_item_label,
            when (gameType) {
                GAME.TYPE_GAME_11X5 -> R.drawable.lot_duplex_item_label_bg_11x5_selector
                GAME.TYPE_GAME_PK8, GAME.TYPE_GAME_PK10 -> R.drawable.lot_duplex_item_label_bg_pk_selector
                else -> R.drawable.lot_duplex_item_label_bg_selector
            }
        )
        holder.setText(R.id.lot_duplex_item_label, item.label)

        //大小单双
        val dxdsVisible = item.dxdsVisible
        //11选5组选胆拖的胆码不需要显示大小单双
        if (dxdsVisible && 0 == position && betTypeId == 586 || betTypeId == 587 || betTypeId == 179 || betTypeId == 182 || betTypeId == 176) {
            holder.setVisible(R.id.lot_duplex_item_dxds_rl, false)
        } else {
            holder.setGone(R.id.lot_duplex_item_dxds_rl, !dxdsVisible)
        }
        //球
        val rv: RecyclerView = holder.getView(R.id.lot_duplex_item_rv)
        rv.layoutManager = GridLayoutManager(context, mSpanCount)
        val adapter = rv.adapter?.notifyDataSetChanged() ?: let {
            rv.adapter = LotDuplexSubAdapter(gameType, betTypeId, mSpanCount, false, item, {})
        }
    }

    override fun convert(holder: BaseViewHolder, item: LotDuplexData, payloads: List<String>) {
        val position = holder.adapterPosition
        when (payloads[0]) {
            PAY_LOAD_BIG -> holder.setItemChildSelected(R.id.lot_duplex_item_big,
                position == mBigSelectedPos)
            PAY_LOAD_SMALL -> holder.setItemChildSelected(R.id.lot_duplex_item_small,
                position == mSmallSelectedPos)
            PAY_LOAD_SINGLE -> holder.setItemChildSelected(R.id.lot_duplex_item_single,
                position == mSingleSelectedPos)
            PAY_LOAD_DOUBLE -> holder.setItemChildSelected(R.id.lot_duplex_item_double,
                position == mDoubleSelectedPos)
            PAY_LOAD_LABEL -> holder.setItemChildSelected(R.id.lot_duplex_item_big,
                position == mBigSelectedPos)
            else -> {
                holder.setItemChildSelected(R.id.lot_duplex_item_big,
                    position == mBigSelectedPos)
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
        betTypeId: Int, balTextList: List<String>?,//龙虎和
        duplexDatas: List<LotDuplexData>?,
    ) {
        this.betTypeId = betTypeId
    }
}