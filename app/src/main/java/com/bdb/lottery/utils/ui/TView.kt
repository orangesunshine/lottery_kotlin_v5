package com.bdb.lottery.utils.ui

import android.content.Context
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TView @Inject constructor(@ApplicationContext private val context: Context){
    fun layoutId2View(layoutId: Int): View {
        return View.inflate(context, layoutId, null)
    }
}