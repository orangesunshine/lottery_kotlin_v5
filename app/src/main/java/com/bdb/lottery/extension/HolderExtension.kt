package com.bdb.lottery.extension

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.viewholder.BaseViewHolder

fun BaseViewHolder.setTextColorStateList(context: Context, @IdRes id: Int, @ColorRes colorId: Int) {
    getView<TextView>(id).setTextColor(context.resources.getColorStateList(colorId))
}

fun BaseViewHolder.setItemChildSelected(@IdRes id: Int, isSelected: Boolean) {
    getView<View>(id).isSelected = isSelected
}

fun BaseViewHolder.setTextSize(
    @IdRes id: Int,
    size: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    getView<TextView>(id).setTextSize(unit, size)
}

fun BaseViewHolder.setUnitedWH(
    @IdRes id: Int,
    width: Float,
    height: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    setWH(id, Sizes.applyDimension(width, unit).toInt(), Sizes.applyDimension(height, unit).toInt())
}

fun BaseViewHolder.setUnitedSize(
    @IdRes id: Int,
    size: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    val sizeInt = Sizes.applyDimension(size, unit).toInt()
    setWH(id, sizeInt, sizeInt)
}

fun BaseViewHolder.setWH(
    @IdRes id: Int,
    size: Int,
) {
    getView<View>(id).layoutParams.run {
        this.width = size
        this.height = size
    }
}

fun BaseViewHolder.setWH(
    @IdRes id: Int,
    width: Int,
    height: Int,
) {
    getView<View>(id).layoutParams.run {
        this.width = width
        this.height = height
    }
}

fun BaseViewHolder.setWidthWithUnit(
    @IdRes id: Int,
    width: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    getView<View>(id).layoutParams.run {
        this.width = Sizes.applyDimension(width, unit).toInt()
    }
}

fun BaseViewHolder.setHeightWithUnit(
    @IdRes id: Int,
    height: Float,
    unit: Int = TypedValue.COMPLEX_UNIT_DIP,
) {
    getView<View>(id).layoutParams.run {
        this.height = Sizes.applyDimension(height, unit).toInt()
    }
}