package com.bdb.lottery.base.module

import com.tencent.mmkv.MMKV
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class LocalModule {
    @Provides
    @Singleton
    fun mmkv(): MMKV {
        return MMKV.defaultMMKV()
    }
}
