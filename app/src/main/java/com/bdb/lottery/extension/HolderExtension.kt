package com.bdb.lottery.extension

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.viewholder.BaseViewHolder

fun BaseViewHolder.setColorStateList(context: Context, @IdRes id: Int, @ColorRes colorId: Int) {
    getView<TextView>(id).setTextColor(context.resources.getColorStateList(colorId))
}

fun BaseViewHolder.setItemChildSelected(@IdRes id: Int, isSelected: Boolean) {
    getView<TextView>(id).isSelected = isSelected
}

fun BaseViewHolder.setTextSize(
    @IdRes id: Int,
    size: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    getView<TextView>(id).setTextSize(unit, size)
}

fun BaseViewHolder.setSize(
    @IdRes id: Int,
    size: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    setWH(id, size, size, unit)
}

fun BaseViewHolder.setWH(
    @IdRes id: Int,
    width: Float,
    height: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    getView<View>(id).layoutParams.run {
        this.width = Sizes.applyDimension(width, unit).toInt()
        this.height = Sizes.applyDimension(height, unit).toInt()
    }
}

fun BaseViewHolder.setW(
    @IdRes id: Int,
    width: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    getView<View>(id).layoutParams.run {
        this.width = Sizes.applyDimension(width, unit).toInt()
    }
}

fun BaseViewHolder.setH(
    @IdRes id: Int,
    height: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    getView<View>(id).layoutParams.run {
        this.height = Sizes.applyDimension(height, unit).toInt()
    }
}