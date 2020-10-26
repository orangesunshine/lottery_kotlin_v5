package com.bdb.lottery.base.module

import com.bdb.lottery.datasource.domain.DomainLocalDataSource
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetModule {
    @Provides
    @Singleton
    fun retrofit(): Retrofit {
        return Retrofits.create(DomainLocalDataSource.getDomain())
    }

    @Provides
    @Singleton
    @Named("Url")
    fun retrofitWithUrl(): Retrofit {
        return Retrofits.create()
    }
}