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

    private var mSelectedPosition: MutableList<Int> = mutableListOf()

    fun setSingleSelectedPos(selectedPosition: Int) {
        mSelectedPosition.clear()
        mSelectedPosition.add(selectedPosition)
    }

    fun notifyUnSelectedAll() {
        if (!mSelectedPosition.isNullOrEmpty()) {
            for (pos in mSelectedPosition) {
                if (data.validIndex(pos))
                    notifyItemChanged(pos)
            }
        }
        mSelectedPosition.clear()
    }

    fun notifyUnSelectedAllWithPayLoads(payload: String = PAY_LOADS_SELECTED) {
        if (!mSelectedPosition.isNullOrEmpty()) {
            for (pos in mSelectedPosition) {
                if (data.validIndex(pos))
                    notifyItemChanged(pos, payload)
            }
        }
        mSelectedPosition.clear()
    }

    //region single单选
    private fun getSingleSelectedPos(): Int {
        return if (!mSelectedPosition.isNullOrEmpty()) mSelectedPosition[0] else -1
    }

    //endregion

    fun notifySelectedPosition(selectedPosition: Int, singleSelected: Boolean = true) {
        //重负选中
        if (mSelectedPosition.contains(selectedPosition)) {
            if (!singleSelected) {//多选当前位置不选中
                mSelectedPosition.remove(selectedPosition)
                if (data.validIndex(selectedPosition))
                    notifyItemChanged(selectedPosition)
            }
            return
        }
        if (singleSelected) {
            //单选清空之前选中位置
            val preSelectedPosition = getSingleSelectedPos()
            mSelectedPosition.clear()
            if (data.validIndex(preSelectedPosition))
                notifyItemChanged(preSelectedPosition)
        }
        //选中当前位置
        mSelectedPosition.add(selectedPosition)
        if (data.validIndex(selectedPosition))
            notifyItemChanged(selectedPosition)
    }

    fun notifySelectedPositionWithPayLoads(
        selectedPosition: Int,
        checkIndex: Boolean = true,
        singleSelected: Boolean = true,
        payload: String = PAY_LOADS_SELECTED
    ) {
        //重负选中
        if (mSelectedPosition.contains(selectedPosition)) {
            if (singleSelected && checkIndex) return
            if (!singleSelected) {//多选当前位置不选中
                mSelectedPosition.remove(selectedPosition)
                if (data.validIndex(selectedPosition))
                    notifyItemChanged(selectedPosition, payload)
                return
            }
        }
        if (singleSelected) {
            //单选清空之前选中位置
            val preSelectedPosition = getSingleSelectedPos()
            mSelectedPosition.clear()
            if (data.validIndex(preSelectedPosition))
                notifyItemChanged(preSelectedPosition, payload)
        }
        //选中当前位置
        mSelectedPosition.add(selectedPosition)
        if (data.validIndex(selectedPosition))
            notifyItemChanged(selectedPosition, payload)
    }

    //判断当前holder是否选中
    fun isItemSelected(holder: VH): Boolean {
        return mSelectedPosition.contains(holder.adapterPosition)
    }

    fun isSelected(): Boolean {
        return !mSelectedPosition.isNullOrEmpty()
    }
}