package com.bdb.lottery.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.bdb.lottery.R

class AspectRatioImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val mMeasureSpec = AspectRatioMeasure.Spec()
    private var mAspectRatio = 1F

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMeasureSpec.width = widthMeasureSpec
        mMeasureSpec.height = heightMeasureSpec
        AspectRatioMeasure.updateMeasureSpec(
            mMeasureSpec,
            mAspectRatio,
            layoutParams,
            paddingLeft + paddingRight,
            paddingTop + paddingBottom
        )
        super.onMeasure(mMeasureSpec.width, mMeasureSpec.height)
    }

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.AspectRatioImageView
        )
        mAspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspectRatio, 1F)
    }
}

class AspectRatioMeasure {
    class Spec {
        var width = 0
        var height = 0
    }

    companion object {
        fun updateMeasureSpec(
            spec: Spec,
            aspectRatio: Float,
            layoutParams: ViewGroup.LayoutParams?,
            widthPadding: Int,
            heightPadding: Int
        ) {
            if (aspectRatio <= 0 || layoutParams == null) {
                return
            }
            if (shouldAdjust(layoutParams.height)) {
                val widthSpecSize = View.MeasureSpec.getSize(spec.width)
                val desiredHeight =
                    ((widthSpecSize - widthPadding) / aspectRatio + heightPadding).toInt()
                val resolvedHeight = View.resolveSize(desiredHeight, spec.height)
                spec.height =
                    View.MeasureSpec.makeMeasureSpec(resolvedHeight, View.MeasureSpec.EXACTLY)
            } else if (shouldAdjust(layoutParams.width)) {
                val heightSpecSize = View.MeasureSpec.getSize(spec.height)
                val desiredWidth =
                    ((heightSpecSize - heightPadding) * aspectRatio + widthPadding).toInt()
                val resolvedWidth = View.resolveSize(desiredWidth, spec.width)
                spec.width =
                    View.MeasureSpec.makeMeasureSpec(resolvedWidth, View.MeasureSpec.EXACTLY)
            }
        }

        private fun shouldAdjust(layoutDimension: Int): Boolean {
            // Note: wrap_content is supported for backwards compatibility, but should not be used.
            return layoutDimension == 0 || layoutDimension == ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}