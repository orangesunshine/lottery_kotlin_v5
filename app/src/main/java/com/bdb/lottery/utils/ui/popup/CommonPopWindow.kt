package com.bdb.lottery.utils.ui.popup

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.bdb.lottery.R
import com.bdb.lottery.utils.ui.size.Sizes
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
open class CommonPopWindow @Inject constructor(
    @ActivityContext private val context: Context,
    private val width: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    private val popDatas: List<CommonPopData>,
    private val topArrow: Boolean? = null
) : TPopupWindow() {

    fun show(anchorView: View) {
        showAtScreenLocation(
            anchorView,
            Gravity.TOP or Gravity.START,
            y = Sizes.dp2px(-8f),
            align = ALIGN_ANCHOR
        )
    }

    fun init(listener: (View) -> Unit) {
        content(width) {
            val root = LinearLayout(context)
            root.orientation = LinearLayout.VERTICAL
            if (!popDatas.isNullOrEmpty()) {
                for (popData in popDatas) {

                }
            }
            root
            val content = LayoutInflater.from(context).inflate(R.layout.lot_jd_money_unit, null)
            content.findViewById<View>(R.id.lot_jd_money_unit_yuan_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_jiao_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_fen_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_li_tv).setOnClickListener(listener)
            content
        }
    }
}