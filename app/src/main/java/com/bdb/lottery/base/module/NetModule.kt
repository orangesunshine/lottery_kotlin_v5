package com.bdb.lottery.base.module

import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetModule {
    @Singleton
    @Provides
    fun retrofit(localDomainDs: DomainLocalDs): Retrofit {
        return Retrofits.create(localDomainDs.getDomain())
    }
}