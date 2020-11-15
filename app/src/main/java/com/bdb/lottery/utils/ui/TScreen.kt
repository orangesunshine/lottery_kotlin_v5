package com.bdb.lottery.utils.ui

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.bdb.lottery.app.BdbApp
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Author: orange
 * @CreateDate: 2019/9/27 16:18
 */

@Singleton
class TScreen @Inject constructor(@ApplicationContext val context: Context) {
    fun screenSize(): Array<Int> {
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