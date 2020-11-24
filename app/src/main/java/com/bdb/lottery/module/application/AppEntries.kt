package com.bdb.lottery.module.application

import com.bdb.lottery.utils.TGame
import com.bdb.lottery.utils.ui.TSize
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.ui.TScreen
import com.bdb.lottery.utils.ui.TUI
import com.bdb.lottery.utils.ui.TView
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@EntryPoint
interface AppEntries {
    fun provideTCache(): TCache
    fun provideTSize(): TSize
    fun provideTGame(): TGame
    fun provideTScreen(): TScreen
    fun provideTView(): TView
}