package com.bdb.lottery.biz.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.base.viewmodel.BaseViewModel

class SplashViewModel @ViewModelInject constructor() : BaseViewModel() {
    var ldDamain = LiveDataWraper<ViewState<String>>()

    fun mock() {

        ldDamain.setData(ViewState("younger"))
    }
}

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