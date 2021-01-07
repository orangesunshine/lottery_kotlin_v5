package com.bdb.lottery.biz.lot.jd.duplex.adapter

import android.view.View
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.setColorStateList
import com.bdb.lottery.extension.setItemChildSelected
import com.bdb.lottery.extension.setSize
import com.bdb.lottery.extension.setTextSize
import com.bdb.lottery.utils.lot.Lots
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.apache.commons.lang3.StringUtils

class LotDuplexSubAdapter constructor(
    private val gameType: Int,
    private val betTypeId: Int,
    private val spanCount: Int,
    private val isLongHuHe: Boolean,//是否龙虎和
    private val lotLotDuplexData: LotDuplexData,
    private val labelBlock: (isSelected: Boolean) -> Unit,
) : BaseSelectedQuickAdapter<String, BaseViewHolder>(
    if (isLongHuHe) R.layout.lot_duplex_sub_item_long_hu else R.layout.lot_duplex_sub_item
) {
    private val isPk10 = GAME.TYPE_GAME_PK10 == gameType || GAME.TYPE_GAME_PK8 == gameType
    override fun convert(holder: BaseViewHolder, item: String) {
        val adapterPosition = holder.adapterPosition
        val ballTextList = lotLotDuplexData.ballTextList
        holder.setText(R.id.lot_duplex_sub_item_tv,
            if (null != ballTextList && adapterPosition < ballTextList.size) ballTextList[adapterPosition] else if (lotLotDuplexData.zeroVisible && adapterPosition < 10) "0$adapterPosition" else adapterPosition.toString())
        val isK3 = GAME.TYPE_GAME_K3 == gameType
        val containsDxds = StringUtils.containsAny(item, "大", "小", "单", "双")
        holder.setTextSize(R.id.lot_duplex_sub_item_tv, when (spanCount) {
            6 -> {
                if (isK3) 40f else 30f
            }
            120 -> {
                if (isK3) 45f else 35f
            }
            else -> {
                if (!isK3 && containsDxds) 44f else 35f
            }
        })
        holder.setSize(R.id.lot_duplex_sub_item_tv, when (spanCount) {
            6 -> {
                if (isK3) 20f else 14f
            }
            120 -> {
                if (isK3) 16f else 18f
            }
            else -> {
                if (item.length > 1) 15f else 18f
            }
        })
        if (isLongHuHe) {
            holder.setImageResource(R.id.lot_duplex_sub_item_iv, when (item) {
                "龙" -> if (isPk10) R.drawable.lot_duplex_sub_item_pk_long_selected else R.drawable.lot_duplex_sub_item_long_selector
                "虎" -> if (isPk10) R.drawable.lot_duplex_sub_item_pk_hu_selected else R.drawable.lot_duplex_sub_item_hu_selector
                "和" -> R.drawable.lot_duplex_sub_item_he_selector
                else -> -1
            })
            holder.setItemChildSelected(R.id.lot_duplex_sub_item_iv, isItemSelected(holder))
        } else {
            holder.setBackgroundResource(R.id.lot_duplex_sub_item_tv, when (gameType) {
                GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> if (containsDxds) R.drawable.lot_duplex_sub_item_pk_circle_selector else R.drawable.lot_duplex_sub_item_pk_round_selector
                GAME.TYPE_GAME_K3 -> R.drawable.lot_duplex_sub_item_k3_selector
                else -> R.drawable.lot_duplex_sub_item_selector
            })
            holder.setItemChildSelected(R.id.lot_duplex_sub_item_tv, isItemSelected(holder))
        }
    }

    override fun convert(holder: BaseViewHolder, item: String, payloads: List<Any>) {
        holder.setItemChildSelected(R.id.lot_duplex_sub_item_tv, isItemSelected(holder))
    }

    init {
        setOnItemClickListener { _: BaseQuickAdapter<*, *>, view: View, position: Int ->
            notifySelectedPositionWithPayLoads(position, singleSelected = false)
            labelBlock.invoke(isSelected())
        }
    }

    //region 大小单双
    //选中大
    fun selectedBig() {

    }

    //选中小
    fun selectedSmall() {

    }

    //选中单
    fun selectedSingle() {

    }

    //选中双
    fun selectedDouble() {

    }
    //endregion

    //冷热
    fun hotVisible(visible: Boolean) {

    }

    //遗漏
    fun leaveVisible(visible: Boolean) {

    }
}