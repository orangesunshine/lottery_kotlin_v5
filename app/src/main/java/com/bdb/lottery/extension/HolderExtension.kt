package com.bdb.lottery.extension

import android.content.Context
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import com.chad.library.adapter.base.viewholder.BaseViewHolder

fun BaseViewHolder.setColorStateList(context: Context, @IdRes id: Int, @ColorRes colorId: Int) {
    getView<TextView>(id).setTextColor(context.resources.getColorStateList(colorId))
}