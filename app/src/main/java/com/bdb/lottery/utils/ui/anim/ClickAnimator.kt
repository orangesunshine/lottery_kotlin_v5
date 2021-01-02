package com.bdb.lottery.utils.ui.anim

import android.animation.ObjectAnimator
import android.view.View
import com.daimajia.androidanimations.library.BaseViewAnimator

class ClickAnimator : BaseViewAnimator() {
    override fun prepare(target: View?) {
        animatorAgent.playTogether(
            ObjectAnimator.ofFloat(target, "scaleX", 0.9f, 1.0f),
            ObjectAnimator.ofFloat(target, "scaleY", 0.9f, 1.0f)
        )
    }
}