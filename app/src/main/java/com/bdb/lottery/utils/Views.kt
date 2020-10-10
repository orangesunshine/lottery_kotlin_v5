package com.bdb.lottery.utils

import android.view.View
import android.view.ViewStub

object Views {

    /**
     * ViewStub占位 动态添加布局文件
     *
     * @param stub
     * @param layoutId
     */
    fun attachStub(stub: ViewStub, layoutId: Int): View? {
        stub?.layoutResource = layoutId
        return stub.inflate()
    }

    /**
     * 动态设置控件宽高
     */
    fun widthNdHeight() {

    }
}