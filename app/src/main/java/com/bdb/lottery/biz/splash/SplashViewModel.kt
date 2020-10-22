package com.bdb.lottery.biz.splash

import android.content.Context
import android.text.TextUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bdb.lottery.R
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.base.viewmodel.BaseViewModel
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.datasource.domain.DomainRepository
import com.bdb.lottery.datasource.string.StringApi
import com.bdb.lottery.extension.isNetUrl
import com.bdb.lottery.extension.notEmpty
import com.bdb.lottery.utils.net.NetAdapter
import com.bdb.lottery.utils.net.NetCallback
import com.bdb.lottery.utils.net.retrofit.Retrofits
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Named

class SplashViewModel @ViewModelInject @Inject constructor(
    @ApplicationContext val context: Context,
    @Named("Url") var retrofit: Retrofit
) : BaseViewModel() {
    var ldDamain = LiveDataWraper<ViewState<String>>()
    val api = retrofit.create(StringApi::class.java)

    fun mock() {
        val apiTxtDomains = HttpConstUrl.API_TXT_DOMAINS
        val filterDomains = apiTxtDomains.filter { it.isNetUrl() }
        if (!filterDomains.isNullOrEmpty()) {
            val domainPath = context.getString(R.string.api_txt_path)
            if (domainPath.notEmpty()) {
                val countDownLatch = CountDownLatch(filterDomains.size)
                for (apiTxtDomain in filterDomains) {
                    Retrofits.observe(
                        api.get(apiTxtDomain + domainPath),
                        object : NetAdapter<String>() {
                            override fun onSuccess(data: String) {
                                super.onSuccess(data)
                            }

                            
                        }
                    )
                }
            }
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