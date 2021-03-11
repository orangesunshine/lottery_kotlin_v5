package com.bdb.lottery.utils.ui.screen

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.bdb.lottery.app.BdbApp

object Screens {
    fun screenSize(): Array<Int> {
        return screenSize(BdbApp.context)
    }

    private fun screenSize(context: Context): Array<Int> {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return arrayOf(point.x, point.y)
    }
}