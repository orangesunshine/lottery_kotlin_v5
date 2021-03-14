package com.bdb.lottery.biz.lot

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdb.lottery.R
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.ui.popup.ALIGN_ANCHOR
import com.bdb.lottery.utils.ui.popup.TPopupWindow
import com.bdb.lottery.utils.ui.size.Sizes
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
open class UnitPopWindow @Inject constructor(@ActivityContext private val context: Context) :
    TPopupWindow() {

    fun show(unitView: View) {
        showAtScreenLocation(
            unitView,
            Gravity.TOP or Gravity.START,
            yOffset = Sizes.dp2px(8f),
            align = ALIGN_ANCHOR
        )
    }

    fun init(listener: (View) -> Unit) {
        content(Sizes.dp2px(100f)) {
            val content = LayoutInflater.from(context).inflate(R.layout.lot_jd_money_unit, null)
            content.findViewById<View>(R.id.lot_jd_money_unit_yuan_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_jiao_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_fen_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_li_tv).setOnClickListener(listener)
            content
        }
    }

    //pattern(1,2,3,4)->元，角，分，厘
    fun setPattern(pattern: String?) {
        val contentView = getContentView()
        val space = pattern.isSpace()
        if (!space) {
            mPopWin.height = Sizes.dp2px(pattern!!.split(",").size * 51f)
        }
        //元
        contentView.findViewById<View>(R.id.lot_jd_money_unit_yuan_tv)
            .visible(space or pattern!!.contains("1"))
        contentView.findViewById<View>(R.id.yuan_line_tv)
            .visible(!space && pattern.contains("1") && pattern.contains("2"))
        //角
        contentView.findViewById<View>(R.id.lot_jd_money_unit_jiao_tv)
            .visible(space or pattern.contains("2"))
        contentView.findViewById<View>(R.id.jiao_line_tv)
            .visible(!space && pattern.contains("2") && pattern.contains("3"))
        //分
        contentView.findViewById<View>(R.id.lot_jd_money_unit_fen_tv)
            .visible(space || pattern.contains("3"))
        contentView.findViewById<View>(R.id.li_line_tv)
            .visible(!space && pattern.contains("3") && pattern.contains("4"))
        //厘
        contentView.findViewById<View>(R.id.lot_jd_money_unit_li_tv)
            .visible(!space && pattern.contains("4"))
    }
}