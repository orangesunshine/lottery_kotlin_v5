package com.bdb.lottery.module.service

import com.bdb.lottery.utils.ui.toast.AbsToast
import com.bdb.lottery.utils.ui.toast.SystemToast
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@InstallIn(ServiceComponent::class)
@Module
abstract class ServiceModule {

    @Binds
    abstract fun provideToast(toast: SystemToast): AbsToast
}