package com.bdb.lottery.datasource.domain

import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class DomainRepository @Inject constructor(@Named("NoBaseUrl") var retrofit1: Retrofit, var retrofit2: Retrofit) {
    fun url(){
        Timber.d("retrofit1: ${retrofit1.baseUrl()}")
        Timber.d("retrofit2: ${retrofit2.baseUrl()}")
    }
}