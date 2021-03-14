package com.bdb.lottery.datasource.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LiveDataWrapper<Data>(init: Data? = null) {
    private val domainMld: MutableLiveData<Data> = MutableLiveData()
    private val domainLd: LiveData<Data> = domainMld

    init {
        init?.let { setData(init) }
    }

    fun setData(data: Data) {
        domainMld.value = data
    }

    fun getLiveData(): LiveData<Data> {
        return domainLd
    }

    fun getData(): Data? {
        return getLiveData().value
    }
}