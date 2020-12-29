package com.bdb.lottery.biz.lot.jd

import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer1Item
import com.bdb.lottery.extension.margin
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class LotPlayAdapter constructor(betTypeDatas: GameBetTypeData?) :
    BaseSelectedQuickAdapter<PlayLayer1Item, BaseViewHolder>(
        R.layout.text_single_item,
        betTypeDatas?.toMutableList()
    ) {
    private val padding = Sizes.dp2px(2.5f)
    private val margin = Sizes.dp2px(8f)

    override fun convert(holder: BaseViewHolder, item: PlayLayer1Item) {
        holder.getView<TextView>(R.id.text_common_tv).run {
            margin(margin * 3, margin / 2, margin * 3, margin / 2)
            gravity = Gravity.CENTER
            setPadding(padding)
            text = item.name
            setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.lot_jd_play_menu_selector
                )
            )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            setBackgroundResource(R.drawable.lot_jd_play_menu_layer1_selector)
            isSelected = isSelected(holder)
        }
    }

    override fun convert(
        holder: BaseViewHolder,
        item: PlayLayer1Item,
        payloads: List<Any>,
    ) {
        holder.getView<TextView>(R.id.text_common_tv).isSelected = isSelected(holder)
    }
}