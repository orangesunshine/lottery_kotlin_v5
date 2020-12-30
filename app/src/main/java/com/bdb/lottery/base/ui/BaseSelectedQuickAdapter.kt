package com.bdb.lottery.base.ui

import androidx.annotation.LayoutRes
import com.bdb.lottery.extension.validIndex
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 自定selected的adapter
 */
abstract class BaseSelectedQuickAdapter<T, VH : BaseViewHolder> constructor(
    @LayoutRes private val layoutResId: Int,
    data: MutableList<T>? = null,
) : BaseQuickAdapter<T, VH>(layoutResId, data) {
    companion object {
        const val PAY_LOADS_SELECTED = "pay_loads_selected"
    }

    private var mSelectedPosition: Int = -1

    fun setSelectedPosition(selectedPosition: Int) {
        mSelectedPosition = selectedPosition
    }

    fun notifyUnSelectedAll() {
        val preSelectedPosition = mSelectedPosition
        mSelectedPosition = -1
        if (data.validIndex(preSelectedPosition))
            notifyItemChanged(preSelectedPosition)
    }

    fun notifyUnSelectedAllWithPayLoads() {
        val preSelectedPosition = mSelectedPosition
        mSelectedPosition = -1
        if (data.validIndex(preSelectedPosition))
            notifyItemChanged(preSelectedPosition, PAY_LOADS_SELECTED)
    }

    fun notifySelectedPosition(selectedPosition: Int) {
        if (mSelectedPosition == selectedPosition) return
        val preSelectedPosition = mSelectedPosition
        mSelectedPosition = selectedPosition
        if (data.validIndex(selectedPosition))
            notifyItemChanged(selectedPosition)
        if (data.validIndex(preSelectedPosition))
            notifyItemChanged(preSelectedPosition)
    }

    fun notifySelectedPositionWithPayLoads(selectedPosition: Int, checkIndex: Boolean = true) {
        if (mSelectedPosition == selectedPosition && checkIndex) return
        val preSelectedPosition = mSelectedPosition
        mSelectedPosition = selectedPosition
        if (data.validIndex(selectedPosition))
            notifyItemChanged(selectedPosition, PAY_LOADS_SELECTED)
        if (data.validIndex(preSelectedPosition))
            notifyItemChanged(preSelectedPosition, PAY_LOADS_SELECTED)
    }

    //判断当前holder是否选中
    fun isSelected(holder: VH): Boolean {
        return mSelectedPosition == holder.adapterPosition
    }
}