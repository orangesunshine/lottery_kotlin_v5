package com.bdb.lottery.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class SmartRefreshLayoutNoFlling(context: Context?, attrs: AttributeSet?) :
    SmartRefreshLayout(context, attrs) {
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        if (target is RecyclerView && velocityY < 0) {
            return !target.canScrollVertically(-1)
        }
        return super.onNestedPreFling(target, velocityX, velocityY)
    }
}