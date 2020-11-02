package com.bdb.lottery.datasource

import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import javax.inject.Inject

open class BaseRemoteDs @Inject constructor() {
    @Inject
    protected lateinit var retrofitWrapper: RetrofitWrapper
}