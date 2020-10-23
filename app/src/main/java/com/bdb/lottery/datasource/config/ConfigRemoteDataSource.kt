package com.bdb.lottery.datasource.config

import android.content.Context
import com.bdb.lottery.R
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.const.IConst
import com.bdb.lottery.datasource.string.StringApi
import com.bdb.lottery.extension.isNetUrl
import com.bdb.lottery.extension.msg
import com.bdb.lottery.extension.nNullEmpty
import dagger.hilt.android.qualifiers.ActivityContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named


class ConfigRemoteDataSource @Inject constructor(
    @ActivityContext private val context: Context,
    @Named("Url") private var retrofit: Retrofit
) {
    val stringApi = retrofit.create(StringApi::class.java)
    val configApi = retrofit.create(ConfigApi::class.java)

    /**
     * 获取域名并回调
     */
    fun getDomainNdCallback(block: (FrontConfigData?) -> Any) {
        val domainPath = context.getString(R.string.api_txt_path)
        val already = AtomicBoolean(false)
        var disposable: Disposable? = null
        if (domainPath.nNullEmpty()) {
            val domainObservables = domainObservables(context)
            if (!domainObservables.isNullOrEmpty()) {
                Observable.mergeArrayDelayError(*domainObservables)
                    .doOnSubscribe { disposable = it }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map {
                        Timber.d("域名配置：${it}")
                        it.split("@")
                    }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        Timber.d("域名配置列表：${it}")
                        Observable.fromIterable(it)
                    }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        Timber.d("域名：${it}")
                        if (disposable?.isDisposed()
                                ?: true
                        ) null else configApi.frontConfig(it + HttpConstUrl.URL_CONFIG_FRONT)
                    }
                    .observeOn(Schedulers.io())
                    .onErrorReturn {
                        Timber.d("域名错误：${it}")
                        val response = BaseResponse<FrontConfigData>()
                        response.code =
                            if (null != it && it is HttpException) it.code() else IConst.DOAMIN_ERROR_CODE
                        response.msg = it.msg
                        response
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.isSuccess() && null != it && null != it.data) {
                            if (already.compareAndSet(false, true)) {
                                Timber.d("onNext__response: ${it}")
                                block(it.data)
                                disposable?.let {
                                    try {
                                        if (!it.isDisposed) it.dispose()
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }
                    }, {
                        Timber.d("onError：${it.msg}")
                    }, {
                        Timber.d("onComplete")
                    })
            }
        }

    }

    fun domainObservables(context: Context): Array<Observable<String>> {
        val domainObservables = mutableListOf<Observable<String>>()
        val domainPath = context.getString(R.string.api_txt_path)
        if (domainPath.nNullEmpty()) {
            val domainsApiTxt = HttpConstUrl.DOMAINS_API_TXT
            if (!domainsApiTxt.isNullOrEmpty()) {

                for (domain in domainsApiTxt) {
                    if (domain.isNetUrl()) {
                        domainObservables.add(stringApi.get(domain + domainPath))
                    }
                }
            }
        }
        return domainObservables?.toTypedArray()
    }
}