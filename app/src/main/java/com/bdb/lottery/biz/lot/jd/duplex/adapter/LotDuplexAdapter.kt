package com.bdb.lottery.biz.lot.jd.duplex.adapter

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.setColorStateList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class LotDuplexAdapter constructor(
    private val gameType: Int,
    private val playId: Int,
    private val balTextList: Array<String>?,//龙虎和
    duplexDatas: List<LotDuplexData>?
) : BaseQuickAdapter<LotDuplexData, BaseViewHolder>(
    R.layout.lot_duplex_item,
    duplexDatas?.toMutableList()
) {
    private val mSpanCount: Int = if (!balTextList.isNullOrEmpty()) {
        if (balTextList.size > 3) 3 else 2
    } else {
        if (GAME.TYPE_GAME_K3 == gameType) 6 else 5
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
        if (dxdsVisible && 0 == position && playId == 586 || playId == 587 || playId == 179 || playId == 182 || playId == 176) {
            holder.setVisible(R.id.lot_duplex_item_dxds_rl, false)
        } else {
            holder.setGone(R.id.lot_duplex_item_dxds_rl, !dxdsVisible)
        }

    }

    //球列表
    fun genBallText() {

    }

    fun initBallList(rv: RecyclerView) {

    }

    //region 大小单双单击事件
    init {
        addChildLongClickViewIds(
            R.id.lot_duplex_item_big,
            R.id.lot_duplex_item_small,
            R.id.lot_duplex_item_single,
            R.id.lot_duplex_item_double
        )
        setOnItemChildClickListener { baseQuickAdapter: BaseQuickAdapter<Any?, BaseViewHolder>, view: View, position: Int ->
            if (position < mSubAdapters.size()) {
                val subAdapter = mSubAdapters[position]
                if (null != subAdapter) {
                    when (view.id) {
                        R.id.lot_duplex_item_big -> subAdapter.selectedBig()
                        R.id.lot_duplex_item_small -> subAdapter.selectedSmall()
                        R.id.lot_duplex_item_single -> subAdapter.selectedSingle()
                        R.id.lot_duplex_item_double -> subAdapter.selectedDouble()
                    }
                }
            }
        }
    }
    //endregion
}