package com.bdb.lottery.datasource.config

import retrofit2.Retrofit


class ConfigRemoteDataSource constructor(private var retrofit: Retrofit) {
    fun getString(){
        retrofit.baseUrl()
    }

}