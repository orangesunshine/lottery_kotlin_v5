package com.bdb.lottery.biz.lot.jd

import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer2Item
import com.bdb.lottery.extension.margin
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.lot_activity.*

open class LotBetAdapter constructor(list: List<PlayLayer2Item>?) :
    BaseSelectedQuickAdapter<PlayLayer2Item, BaseViewHolder>(
        R.layout.text_single_item, list?.toMutableList()
    ) {
    private val padding = Sizes.dp2px(4f)
    private val margin = Sizes.dp2px(8f)

    override fun convert(
        holder: BaseViewHolder,
        item: PlayLayer2Item,
    ) {
        holder.getView<TextView>(R.id.text_common_tv).run {
            margin(margin * 2, margin / 2, margin * 2, margin / 2)
            gravity = Gravity.CENTER
            setPadding(padding)
            text = item.betName
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