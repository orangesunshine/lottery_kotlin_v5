package com.bdb.lottery.extension

import android.view.View
import android.view.ViewStub
import androidx.annotation.LayoutRes

fun ViewStub.attach(@LayoutRes layoutId: Int): View {
    layoutResource = layoutId
    return inflate()
}