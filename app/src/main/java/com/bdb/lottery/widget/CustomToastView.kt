package com.bdb.lottery.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
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
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(
                tScreen.screenSize()[0] - SPACING,
                MeasureSpec.AT_MOST
            ), heightMeasureSpec
        )
    }

    fun setToastDrawable(@DrawableRes drRes: Int) {
        setToastDrawable(ContextCompat.getDrawable(context, drRes))
    }

    //设置top dr
    private fun setToastDrawable(dr: Drawable?) {
        dr?.setBounds(0, 0, tSize.dp2px(48f), tSize.dp2px(48f))
        mToastTv.setCompoundDrawables(null, dr, null, null);
        mToastTv.compoundDrawablePadding = tSize.dp2px(12f)
    }

    init {
        minimumWidth = tSize.dp2px(190f)
        setBackgroundResource(R.drawable.toast_bg)
        setPadding(tSize.dp2px(12f))
        mToastTv = TextView(getContext());
        mToastTv.gravity = Gravity.CENTER
        mToastTv.setTextColor(Color.parseColor("#eeeeee"))
        mToastTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        addView(mToastTv, lp)
    }

    fun setText(text: CharSequence?) {
        mToastTv.text = text
    }
}