package com.bdb.lottery.biz.lot.jd.single

import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.extension.margin
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.viewholder.BaseViewHolder

open class LotBetAdapter constructor(list: List<BetItem>?) :
    BaseSelectedQuickAdapter<BetItem, BaseViewHolder>(
        R.layout.text_single_item, list?.toMutableList()
    ) {
    private val dp4 = Sizes.dp2px(4f)

    override fun convert(
        holder: BaseViewHolder,
        item: BetItem,
    ) {
        val betName = item.betName
        val marginHorizontal = if (betName.length > 5) 0 else 2 * dp4
        holder.getView<TextView>(R.id.text_common_tv).run {
            margin(marginHorizontal, dp4, marginHorizontal, dp4)
            gravity = Gravity.CENTER
            setPadding(dp4 * 2, dp4, dp4 * 2, dp4)
            text = betName
            setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.lot_jd_play_menu_selector
                )
            )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
            setBackgroundResource(R.drawable.lot_jd_play_menu_layer2_selector)
        }
    }
}