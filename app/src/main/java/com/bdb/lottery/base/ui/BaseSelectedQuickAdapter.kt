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
    updateBlock: ((list: ArrayList<Int>) -> Unit)? = null
) : BaseQuickAdapter<T, VH>(layoutResId, data) {
    companion object {
        const val PAY_LOADS_SELECTED = "pay_loads_selected"
    }

    private var mSelectedPosition: MutableList<Int> =
        if (null == updateBlock) mutableListOf() else UpdateArrayList(updateBlock)

    //region single单选

    fun setSingleSelectedPos(selectedPosition: Int) {
        mSelectedPosition.clear()
        mSelectedPosition.add(selectedPosition)
    }

    private fun getSingleSelectedPos(): Int {
        return if (!mSelectedPosition.isNullOrEmpty()) mSelectedPosition[0] else -1
    }

    //判断当前holder是否选中
    fun isItemSelected(holder: VH): Boolean {
        return mSelectedPosition.contains(holder.adapterPosition)
    }

    //region 选中单选或多选
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
        //重复选中
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
    //endregion

    //region 取消选中
    fun notifySingleUnSelectedPosition(unselectedPosition: Int) {
        if (!mSelectedPosition.isNullOrEmpty() && mSelectedPosition.contains(unselectedPosition)) {
            mSelectedPosition.remove(unselectedPosition)
            notifyItemChanged(unselectedPosition)
        }
    }

    fun notifySingleUnSelectedPositionWithPayLoads(
        unselectedPosition: Int,
        payload: String = PAY_LOADS_SELECTED
    ) {
        if (!mSelectedPosition.isNullOrEmpty() && mSelectedPosition.contains(unselectedPosition)) {
            mSelectedPosition.remove(unselectedPosition)
            notifyItemChanged(unselectedPosition, payload)
        }
    }
    //endregion
    //endregion

    //region 多选
    fun getSelectedPositions(): MutableList<Int> {//返回选中的下标列表
        return mSelectedPosition
    }

    fun isSelected(): Boolean {
        return !mSelectedPosition.isNullOrEmpty()
    }

    //region 多选
    fun notifyMultiSelectedPositionWithPayLoads(
        selectedPositions: List<Int>,
        payload: String = PAY_LOADS_SELECTED
    ) {
        if (selectedPositions.isNullOrEmpty()) return
        mSelectedPosition.clear()
        mSelectedPosition.addAll(selectedPositions)
        notifyItemRangeChanged(0, itemCount, payload)
    }
    //endregion

    //region 全部取消选中
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
    //endregion
    //endregion
}

class UpdateArrayList<E> constructor(private val updateBlock: ((list: ArrayList<E>) -> Unit)? = null) :
    ArrayList<E>() {
    override fun add(element: E): Boolean {
        val pre = isNotEmpty()
        return super.add(element).also {
            if (pre != isNotEmpty()) updateBlock?.invoke(this)
        }
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val pre = isNotEmpty()
        return super.addAll(elements).also {
            if (pre != isNotEmpty()) updateBlock?.invoke(this)
        }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val pre = isNotEmpty()
        return super.addAll(index, elements).also {
            if (pre != isNotEmpty()) updateBlock?.invoke(this)
        }
    }

    override fun add(index: Int, element: E) {
        val pre = isNotEmpty()
        super.add(index, element)
        if (pre != isNotEmpty()) updateBlock?.invoke(this)
    }

    override fun clear() {
        val pre = isNotEmpty()
        super.clear()
        if (pre != isNotEmpty()) updateBlock?.invoke(this)
    }

    override fun remove(element: E): Boolean {
        val pre = isNotEmpty()
        return super.remove(element).also {
            if (pre != isNotEmpty()) updateBlock?.invoke(this)
        }
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val pre = isNotEmpty()
        return super.removeAll(elements).also {
            if (pre != isNotEmpty()) updateBlock?.invoke(this)
        }
    }

    override fun removeAt(index: Int): E {
        val pre = isNotEmpty()
        return super.removeAt(index).also {
            if (pre != isNotEmpty()) updateBlock?.invoke(this)
        }
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        val pre = isNotEmpty()
        super.removeRange(fromIndex, toIndex)
        if (pre != isNotEmpty()) updateBlock?.invoke(this)
    }
}