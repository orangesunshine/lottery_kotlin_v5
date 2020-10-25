package com.bdb.lottery.extension

import android.view.View
import android.view.ViewStub
import android.view.animation.AlphaAnimation
import androidx.annotation.LayoutRes
val apperAnim = AlphaAnimation(0f, 1f).apply { duration = 1000 }
val disapperAnim = AlphaAnimation(1f, 0f).apply { duration = 1000 }

fun ViewStub.attach(@LayoutRes layoutId: Int): View {
    layoutResource = layoutId
    return inflate()
}

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.alphaVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
    startAnimation(if(visible) apperAnim else disapperAnim)
}