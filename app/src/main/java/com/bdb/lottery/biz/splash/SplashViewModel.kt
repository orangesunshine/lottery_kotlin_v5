package com.bdb.lottery.biz.splash

import android.content.Context
import android.text.TextUtils
import androidx.core.net.toUri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.base.viewmodel.BaseViewModel
import com.bdb.lottery.datasource.config.ConfigRemoteDataSource
import com.bdb.lottery.datasource.config.FrontConfig.saveDomain
import com.bdb.lottery.extension.isNetUrl
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject

class SplashViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    val config: ConfigRemoteDataSource
) : BaseViewModel() {
    var ldDamain = LiveDataWraper<ViewState<String>>()
    fun mock() {
        config.getDomainNdCallback {
            val scheme = it?.WebMobileUrl?.toUri()?.scheme
            val host = it?.WebMobileUrl?.toUri()?.host
            val authority = it?.WebMobileUrl?.toUri()?.authority
            val domain = scheme + "://" + if (TextUtils.isEmpty(host)) authority else host
            saveDomain(domain)
        }
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