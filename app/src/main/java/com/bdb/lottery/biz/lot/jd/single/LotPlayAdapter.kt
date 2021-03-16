package com.bdb.lottery.biz.lot.jd.single

import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayItem
import com.bdb.lottery.extension.margin
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class LotPlayAdapter constructor(betTypeDatas: GameBetTypeData?) :
    BaseSelectedQuickAdapter<PlayItem, BaseViewHolder>(
        R.layout.text_single_item,
        betTypeDatas?.toMutableList()
    ) {
    private val dp4 = Sizes.dp2px(4f)

    override fun convert(holder: BaseViewHolder, item: PlayItem) {
        val name = item.name
        val marginHorizontal =
            if (name.length > 5) 0 else if (name.length > 4) 2 * dp4 else 4 * dp4
        holder.getView<TextView>(R.id.text_common_tv).run {
            margin(marginHorizontal, dp4, marginHorizontal, dp4)
            gravity = Gravity.CENTER
            setPadding(dp4 * 2, dp4, dp4 * 2, dp4)
            text = name
            setTextColor(
                ContextCompat.getColorStateList(
                    context,
                    R.color.lot_jd_play_menu_color_selector
                )
            )
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            setBackgroundResource(R.drawable.lot_jd_play_menu_layer1_selector)
            isSelected = isItemSelected(holder)
        }
    }

    override fun convert(
        holder: BaseViewHolder,
        item: PlayItem,
        payloads: List<Any>,
    ) {
        holder.getView<TextView>(R.id.text_common_tv).isSelected = isItemSelected(holder)
    }
}