package com.bdb.lottery.module.activity

import com.bdb.lottery.utils.ui.toast.AbsToast
import com.bdb.lottery.utils.ui.toast.WindowManagerToast
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class ActivityModule {

    @Binds
    abstract fun provideToast(toast: WindowManagerToast): AbsToast
}