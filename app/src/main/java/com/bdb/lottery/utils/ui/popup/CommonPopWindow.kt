package com.bdb.lottery.utils.ui.popup

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bdb.lottery.R
import com.bdb.lottery.utils.ui.size.Sizes
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
open class CommonPopWindow @Inject constructor(
    @ActivityContext private val context: Context,
) : TPopupWindow() {

    fun init(
        popDatas: List<CommonPopData>,
        width: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    ) {
        content(width) {
            val root = LinearLayout(context)
            root.orientation = LinearLayout.VERTICAL
            root.gravity = Gravity.CENTER_HORIZONTAL
            if (!popDatas.isNullOrEmpty()) {
                for (popData in popDatas) {
                    attachItem(root, popData)
                }
                if (root.childCount > 0) {
                    root.removeViewAt(root.childCount - 1)
                }
            }
            root
        }
    }

    private val dp40 = Sizes.dp2px(40f)
    private val tvColor = Color.parseColor("#EBEBEB")
    private val bgColor = Color.parseColor("#454852")
    private fun attachItem(
        root: ViewGroup,
        popData: CommonPopData,
        width: Int = LinearLayout.LayoutParams.MATCH_PARENT
    ) {
        val imgRes = popData.imgRes
        val text = popData.text
        val click = popData.click
        val tv = TextView(context)
        tv.layoutParams = LinearLayout.LayoutParams(width, dp40)
        tv.text = text
        tv.setTextColor(tvColor)
        tv.gravity = Gravity.CENTER
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        imgRes?.let {
            tv.setCompoundDrawables(ContextCompat.getDrawable(context, imgRes), null, null, null)
        }
        click?.let {
            tv.setOnClickListener {
                click.invoke(text)
            }
        }
        root.addView(tv)
        val line = View(context)
        line.layoutParams =
            LinearLayout.LayoutParams(width, Sizes.dp2px(0.5f))
        line.setBackgroundColor(bgColor)
        root.addView(line)
    }

    fun showAtScreenLocation(
        parent: View,
        xOffset: Int = 0,
        yOffset: Int = 0,
        gravity: Int = Gravity.TOP or Gravity.START,
        @ALIGN align: Int = ALIGN_RIGHT,
    ) {
        val windowPos = IntArray(2)
        val up = needShowUp(parent, mContent, windowPos, xOffset, yOffset, align)
        mContent.setBackgroundResource(if (up) R.drawable.popwin_bg_up else R.drawable.popwin_bg_down)
        mPopWin.showAtLocation(parent, gravity, windowPos[0], windowPos[1])
    }
}