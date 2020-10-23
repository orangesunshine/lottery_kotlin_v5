package com.bdb.lottery.base.module

import com.bdb.lottery.datasource.config.FrontConfig
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(ApplicationComponent::class)
class NetModule {
    @Provides
    fun retrofit(): Retrofit {
        return Retrofits.create(FrontConfig.getDomain())
    }

    @Provides
    @Named("Url")
    fun retrofitWithUrl(): Retrofit {
        return Retrofits.create()
    }
}