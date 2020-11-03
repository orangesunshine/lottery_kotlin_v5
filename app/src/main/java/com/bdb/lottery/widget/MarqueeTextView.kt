package com.bdb.lottery.widget

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MarqueeTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        //设置单行
        setSingleLine();
        //设置Ellipsize(代码设置两端多出alph效果)
        ellipsize = TextUtils.TruncateAt.MARQUEE;
        //获取焦点
        isFocusable = true;
        //走马灯的重复次数，-1代表无限重复
        marqueeRepeatLimit = -1;
        //强制获得焦点
        isFocusableInTouchMode = true;
    }

    override fun isFocused(): Boolean {
        return true
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus)
            super.onWindowFocusChanged(hasWindowFocus)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }
}