package com.bdb.lottery.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.bdb.lottery.R
import com.bdb.lottery.module.AppEntries
import com.bdb.lottery.utils.ui.TScreen
import com.bdb.lottery.utils.ui.TSize
import dagger.hilt.android.EntryPointAccessors

class CustomToastView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val mToastTv: TextView

    private val tSize: TSize =
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTSize()
    private val tScreen: TScreen =
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTScreen()
    private val SPACING: Int = tSize.dp2px(80f)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMaxSpec =
            MeasureSpec.makeMeasureSpec(
                tScreen.screenSize()[0] - SPACING,
                MeasureSpec.AT_MOST
            )
        super.onMeasure(widthMaxSpec, heightMeasureSpec)
    }

    init {
        setBackgroundResource(R.drawable.toast_bg)
        setPadding(tSize.dp2px(12f))
        mToastTv = TextView(getContext());
        mToastTv.gravity = Gravity.CENTER
        mToastTv.setTextColor(Color.parseColor("#DE000000"))
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        addView(mToastTv, lp)
    }

    fun setText(text: CharSequence) {
        mToastTv.setText(text)
    }
}