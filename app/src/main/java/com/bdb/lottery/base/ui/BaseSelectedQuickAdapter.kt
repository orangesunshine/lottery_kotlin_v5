package com.bdb.lottery.base.ui

import androidx.annotation.LayoutRes
import com.bdb.lottery.extension.*
import com.bdb.lottery.extension.setItemChildSelected
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

    override fun convert(holder: VH, item: T) {
        holder.setItemSelected(isItemSelected(holder))
    }

    override fun convert(holder: VH, item: T, payloads: List<Any>) {
        if (PAY_LOADS_SELECTED.equalsPayLoads(payloads)) {
            holder.setItemSelected(isItemSelected(holder))
        }
    }

    private var mSelectedPosition: MutableList<Int> =
        if (null == updateBlock) mutableListOf() else UpdateArrayList(updateBlock)

    private fun clearNotNotify() {
        if (mSelectedPosition is UpdateArrayList) (mSelectedPosition as UpdateArrayList<Int>).clearNotNotify() else mSelectedPosition.clear()
    }

    //region single单选
    fun setSingleSelectedPos(selectedPosition: Int) {
        clearNotNotify()
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
            clearNotNotify()
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
            clearNotNotify()
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
        clearNotNotify()
        mSelectedPosition.addAll(selectedPositions)
        notifyItemRangeChanged(0, itemCount, payload)
    }
    //endregion

    //region 全部取消选中
    fun notifyUnSelectedAll() {
        if (!mSelectedPosition.isNullOrEmpty()) {
            val tmpList = ArrayList<Int>(mSelectedPosition)
            mSelectedPosition.clear()
            for (pos in tmpList) {
                if (data.validIndex(pos))
                    notifyItemChanged(pos)
            }
        }
    }

    fun notifyUnSelectedAllWithPayLoads(payload: String = PAY_LOADS_SELECTED) {
        if (!mSelectedPosition.isNullOrEmpty()) {
            val tmpList = ArrayList<Int>(mSelectedPosition)
            mSelectedPosition.clear()
            for (pos in tmpList) {
                if (data.validIndex(pos))
                    notifyItemChanged(pos, payload)
            }
        }
    }
    //endregion
    //endregion
}

class UpdateArrayList<E> constructor(private val updateBlock: ((list: ArrayList<E>) -> Unit)? = null) :
    ArrayList<E>() {
    private fun <T> onMultiUpdate(
        updateOperate: () -> T
    ): T {
        val pre = ArrayList<E>(this)
        val ret: T = updateOperate()
        if (!pre.equals(this)) updateBlock?.invoke(this)
        return ret
    }


    override fun add(element: E): Boolean {
        return onMultiUpdate { super.add(element) }
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return onMultiUpdate { super.addAll(elements) }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        return onMultiUpdate { super.addAll(index, elements) }
    }

    override fun add(index: Int, element: E) {
        onMultiUpdate { super.add(index, element) }
    }

    fun clearNotNotify() {
        super.clear()
    }

    override fun clear() {
        onMultiUpdate { super.clear() }
    }

    override fun remove(element: E): Boolean {
        return onMultiUpdate { super.remove(element) }
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return onMultiUpdate { super.removeAll(elements) }
    }

    override fun removeAt(index: Int): E {
        return onMultiUpdate { super.removeAt(index) }
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        onMultiUpdate { super.removeRange(fromIndex, toIndex) }
    }
}