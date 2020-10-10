package com.bdb.lottery.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.bdb.lottery.utils.Apps.getApp

object Sizes {
    inline fun scale(context: Context? = getApp()?.applicationContext): Float {
        context?.run {
            return resources.displayMetrics.density
        }
        return -1f
    }

    inline fun fontScale(context: Context? = getApp()?.applicationContext): Float {
        context?.run {
            return resources.displayMetrics.scaledDensity
        }
        return -1f
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    fun dp2px(dpValue: Float): Int {
        dpValue?.let {
            return (it * scale() + 0.5f).toInt()
        }
        return -1
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    fun px2dp(pxValue: Float): Int {
        pxValue?.let {
            return (it / scale() + 0.5f).toInt()
        }
        return -1
    }

    /**
     * Value of sp to value of px.
     *
     * @param spValue The value of sp.
     * @return value of px
     */
    fun sp2px(spValue: Float): Int {
        spValue?.let {
            return (it / fontScale() + 0.5f).toInt()
        }
        return -1
    }

    /**
     * Value of px to value of sp.
     *
     * @param pxValue The value of px.
     * @return value of sp
     */
    fun px2sp(pxValue: Float): Int {
        pxValue?.let {
            return (it / fontScale() + 0.5f).toInt()
        }
        return -1
    }

    /**
     * 获取屏幕密度
     *
     * @return
     */
    fun getDensity(): Float {
        return Resources.getSystem().displayMetrics.density
    }

    /**
     * 获取font屏幕密度
     *
     * @return
     */
    fun getScaledDensity(): Float {
        return Resources.getSystem().displayMetrics.scaledDensity
    }


    /**
     * Converts an unpacked complex data value holding a dimension to its final floating
     * point value. The two parameters <var>unit</var> and <var>value</var>
     * are as in [TypedValue.TYPE_DIMENSION].
     *
     * @param value The value to apply the unit to.
     * @param unit  The unit to convert from.
     * @return The complex floating point value multiplied by the appropriate
     * metrics depending on its unit.
     */
    fun applyDimension(value: Float, unit: Int): Float {
        val context = getApp()
        context?.run {
            resources.displayMetrics
        }?.let {
            when (unit) {
                TypedValue.COMPLEX_UNIT_PX -> return value
                TypedValue.COMPLEX_UNIT_DIP -> return value * it.density
                TypedValue.COMPLEX_UNIT_SP -> return value * it.scaledDensity
                TypedValue.COMPLEX_UNIT_PT -> return value * it.xdpi * (1.0f / 72)
                TypedValue.COMPLEX_UNIT_IN -> return value * it.xdpi
                TypedValue.COMPLEX_UNIT_MM -> return value * it.xdpi * (1.0f / 25.4f)
                else -> return -1f
            }
        } ?: return -1f
    }

    /**
     * Force get the size of view.
     *
     * error.g.
     * <pre>
     * SizeUtils.forceGetViewSize(view, new SizeUtils.onGetSizeListener() {
     * Override
     * public void onGetSize(final View view) {
     * view.getWidth();
     * }
     * });
    </pre> *
     *
     * @param view     The view.
     * @param listener The get size listener.
     */
    fun forceGetViewSize(view: View, listener: onGetSizeListener?) {
        view.post { listener?.onGetSize(view) }
    }

    /**
     * Return the width of view.
     *
     * @param view The view.
     * @return the width of view
     */
    fun getMeasuredWidth(view: View): Int {
        return measureView(view)[0]
    }

    /**
     * Return the height of view.
     *
     * @param view The view.
     * @return the height of view
     */
    fun getMeasuredHeight(view: View): Int {
        return measureView(view)[1]
    }

    /**
     * Measure the view.
     *
     * @param view The view.
     * @return arr[0]: view's width, arr[1]: view's height
     */
    fun measureView(view: View): IntArray {
        var lp = view.layoutParams ?: ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
        val lpHeight = lp.height
        val heightSpec = if (lpHeight > 0) {
            View.MeasureSpec.makeMeasureSpec(
                lpHeight,
                View.MeasureSpec.EXACTLY
            )
        } else {
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        }
        view.measure(widthSpec, heightSpec)
        return intArrayOf(view.measuredWidth, view.measuredHeight)
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface onGetSizeListener {
        fun onGetSize(view: View?)
    }
}