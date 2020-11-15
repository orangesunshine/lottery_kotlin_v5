package com.bdb.lottery.module

import com.bdb.lottery.utils.TGame
import com.bdb.lottery.utils.ui.TSize
import com.bdb.lottery.utils.cache.TCache
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@EntryPoint
interface AppEntries {
    fun provideCache(): TCache
    fun provideSize(): TSize
    fun provideGame(): TGame
}