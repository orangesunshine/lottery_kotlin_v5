package com.bdb.lottery.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TPermision @Inject constructor(@ApplicationContext private val context: Context) {
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun isGrantedDrawOverlays(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun toastType(): Int {
        var type = -1
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            type = WindowManager.LayoutParams.TYPE_TOAST
        } else if (isGrantedDrawOverlays()) {
            type =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        }
        return type

    }
}