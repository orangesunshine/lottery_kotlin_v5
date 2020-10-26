package com.bdb.lottery.datasource.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LiveDataWraper<Data>() {
    private val domainMld: MutableLiveData<Data> = MutableLiveData()
    private val domainLd: LiveData<Data> = domainMld

    fun setData(data: Data) {
        domainMld.value = data
    }

    fun getLiveData(): LiveData<Data> {
        return domainLd
    }
}