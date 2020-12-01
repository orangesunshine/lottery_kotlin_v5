package com.bdb.lottery.module.application

import com.bdb.lottery.utils.game.TGame
import com.bdb.lottery.utils.ui.size.TSize
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.ui.screen.TScreen
import com.bdb.lottery.utils.ui.view.TView
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