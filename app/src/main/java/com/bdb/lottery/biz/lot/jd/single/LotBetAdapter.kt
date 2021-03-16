package com.bdb.lottery.biz.lot.jd.single

import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
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
        holder.getView<TextView>(R.id.text_common_tv).run {
            margin(2 * dp4, dp4, 2 * dp4, dp4)
            gravity = Gravity.CENTER
            setPadding(dp4)
            text = item.betName
            setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.lot_jd_play_menu_color_selector
                )
            )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
            setBackgroundResource(R.drawable.lot_jd_play_menu_layer2_selector)
        }
    }
}