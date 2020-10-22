package com.bdb.lottery.datasource.domain

import com.bdb.lottery.datasource.string.StringApi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class DomainRepository @Inject constructor(@Named("Url") var retrofit: Retrofit) {
    fun getDomain(url: String) {
        Timber.d("getDomain" + Thread.currentThread().name)
        retrofit.create(StringApi::class.java).get(url)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe({
                Timber.d("doOnSubscribe：" + Thread.currentThread().name)
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Timber.d("onNext：" + Thread.currentThread().name) },
                { Timber.d("onError：" + Thread.currentThread().name+ ", error: ${it.message}, ${it::class.java.simpleName}") },
                { Timber.d("onComplete：" + Thread.currentThread().name) })
    }
}