package com.bdb.lottery.extension

import android.view.View
import android.view.ViewStub
import androidx.annotation.LayoutRes

fun ViewStub.attach(@LayoutRes layoutId: Int): View {
    layoutResource = layoutId
    return inflate()
}

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}