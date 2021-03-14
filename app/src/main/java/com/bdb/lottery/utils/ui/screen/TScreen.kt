package com.bdb.lottery.utils.ui.screen

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.bdb.lottery.app.BdbApp
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Author: orange
 * @CreateDate: 2019/9/27 16:18
 */

@Singleton
class TScreen @Inject constructor(@ApplicationContext val context: Context) {

    fun screenWidth(): Int {
        return Screens.screenSize()[0]
    }

    fun screenHeight(): Int {
        return Screens.screenSize()[1]
    }
}