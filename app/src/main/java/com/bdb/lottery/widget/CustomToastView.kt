package com.bdb.lottery.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bdb.lottery.R
import com.bdb.lottery.utils.ui.screen.Screens
import com.bdb.lottery.utils.ui.size.Sizes

class CustomToastView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val mToastTv: TextView

    private val SPACING: Int = Sizes.dp2px(80f)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(
                Screens.screenSize()[0] - SPACING,
                MeasureSpec.AT_MOST
            ), heightMeasureSpec
        )
    }

    fun setToastDrawable(@DrawableRes drRes: Int) {
        setToastDrawable(ContextCompat.getDrawable(context, drRes))
    }

    //设置top dr
    private fun setToastDrawable(dr: Drawable?) {
        dr?.setBounds(0, 0, Sizes.dp2px(36f), Sizes.dp2px(36f))
        mToastTv.setCompoundDrawables(null, dr, null, null);
        mToastTv.compoundDrawablePadding = Sizes.dp2px(8f)
    }

    init {
        minimumWidth = Sizes.dp2px(190f)
        setBackgroundResource(R.drawable.toast_bg)
        setPadding(Sizes.dp2px(12f))
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

    fun tipsText(text: CharSequence?) {
        mToastTv.setCompoundDrawables(null, null, null, null);
        mToastTv.compoundDrawablePadding = 0
        val spanned =
            SpannableStringBuilder("温馨提示").append(System.getProperty("line.separator")).append(text)
        spanned
            .setSpan(AbsoluteSizeSpan(18, true), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spanned.setSpan(ForegroundColorSpan(Color.WHITE), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        mToastTv.text = spanned
    }
}