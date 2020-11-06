package com.bdb.lottery.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAtViewPager2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var startX = 0
    private var startY = 0
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = Math.abs(endX - startX)
                val disY = Math.abs(endY - startY)
                val isDrag = disX < disY || disX < touchSlop
                parent.requestDisallowInterceptTouchEvent(isDrag)
            }

        }
        return super.dispatchTouchEvent(ev)
    }
}