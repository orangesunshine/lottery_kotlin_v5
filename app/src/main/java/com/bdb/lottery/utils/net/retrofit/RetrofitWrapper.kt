package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.datasource.domain.DomainRemoteDs
import com.bdb.lottery.extension.code
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.msg
import com.bdb.lottery.extension.toast
import com.bdb.lottery.utils.cache.Cache
import com.google.gson.GsonBuilder
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject

@ActivityRetainedScoped
class RetrofitWrapper @Inject constructor(
    private val domainLocalDs: DomainLocalDs,
    private val domainRemoteDs: DomainRemoteDs,
) {
    //rxjava 简化
    fun <Data> observe(
        observable: Observable<BaseResponse<Data?>>,
        success: ((Data?) -> Unit)? = null,
        error: ((code: Int, msg: String?) -> Unit)? = null,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
        viewState: LiveDataWraper<ViewState?>? = null,
    ) {
        observable
            .subscribeOn(Schedulers.io())
            .apply {
                if (null != onStart || null != viewState) {
                    doOnSubscribe {
                        onStart?.invoke(it)
                        viewState?.setData(ViewState(true))
                    }
                    subscribeOn(AndroidSchedulers.mainThread())
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                success?.invoke(it.data)
            },
                {
                    val code = it.code
                    val msg = it.msg
                    Timber.d("observe__onError__throwable: ${it}, \\n msg: ${msg}, code: ${code}")
                    BdbApp.context.toast(msg)
                    error?.invoke(code, msg)
                    if (code >= 500) domainLocalDs.clearDomain()
                    viewState?.setData(ViewState(false))
                    complete?.invoke()
                },
                {
                    viewState?.setData(ViewState(false))
                    complete?.invoke()
                })
        domainRemoteDs.getDomain {
        }
    }

    //错误返回有用数据
    fun <Data> observeErrorData(
        observable: Observable<BaseResponse<Data?>>,
        success: ((Data?) -> Unit)? = null,
        error: ((BaseResponse<*>) -> Unit)? = null,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
        viewState: LiveDataWraper<ViewState?>? = null,
    ) {
        domainRemoteDs.getDomain {
            observable.subscribeOn(Schedulers.io())
                .apply {
                    if (null != onStart || null != viewState) {
                        doOnSubscribe {
                            onStart?.invoke(it)
                            viewState?.setData(ViewState(true))
                        }
                        subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    success?.invoke(it.data)
                },
                    {
                        val code = it.code
                        val msg = it.msg
                        Timber.d("observeErrorData__onError__throwable: ${it}, \\n msg: ${msg}, code: ${code}")
                        BdbApp.context.toast(msg)
                        if (it is ApiException) {
                            error?.invoke(it.response)
                        } else {
                            error?.invoke(BaseResponse(code, msg, null))
                        }
                        if (code >= 500) domainLocalDs.clearDomain()
                        viewState?.setData(ViewState(false))
                        complete?.invoke()
                    },
                    {
                        viewState?.setData(ViewState(false))
                        complete?.invoke()
                    })
        }
    }

    //预加载
    fun <Data> preload(
        cacheKey: String,
        observable: Observable<BaseResponse<Data?>>,
        success: ((Data?) -> Unit)? = null,
    ) {
        Cache.putString(cacheKey)//清空缓存
        observe(
            observable,
            {
                it?.let {
                    Cache.putString(
                        cacheKey,
                        GsonBuilder().create().toJson(it)
                    )
                    success?.invoke(it)
                }
            })
    }

    //优先读取缓存，无缓存网络请求
    inline fun <reified Data> inlineCacheBeforeLoad(
        cacheKey: String,
        observable: Observable<BaseResponse<Data?>>,
        noinline success: ((Data?) -> Unit)? = null,
    ) {
        //优先缓存
        val cache = Cache.getString(cacheKey)
        Timber.d("cache: ${cache}")
        if (!cache.isSpace()) {
            try {
                val fromJson: Data = GsonBuilder().create().fromJson(cache, Data::class.java)
                Timber.d("cacheKey: ${cacheKey}==>success: ${fromJson}")
                success?.invoke(fromJson)
            } catch (e: Exception) {
                Timber.d("cacheKey: ${cacheKey}==>error:${e.msg}")
                observe(observable, {
                    it?.let {
                        Cache.putString(
                            cacheKey,
                            GsonBuilder().create().toJson(it)
                        )
                        success?.invoke(it)
                    }
                })
            }
        } else {
            observe(observable, {
                it?.let {
                    Cache.putString(
                        cacheKey,
                        GsonBuilder().create().toJson(it)
                    )
                    success?.invoke(it)
                }
            })
        }
    }

    //优先读取缓存，无缓存网络请求
    fun <Data> cacheBeforeLoad(
        cacheKey: String,
        observable: Observable<BaseResponse<Data?>>,
        success: ((Data?) -> Unit)? = null,
        type: Type? = null,
    ) {
        //优先缓存
        val cache = Cache.getString(cacheKey)
        Timber.d("cache: ${cache}")
        if (!cache.isSpace()) {
            try {
                val fromJson: Data = GsonBuilder().create().fromJson(cache, type)
                Timber.d("cacheKey: ${cacheKey}==>success: ${fromJson}")
                success?.invoke(fromJson)
            } catch (e: Exception) {
                Timber.d("cacheKey: ${cacheKey}==>error:${e.msg}")
                observe(observable, {
                    it?.let {
                        Cache.putString(
                            cacheKey,
                            GsonBuilder().create().toJson(it)
                        )
                        success?.invoke(it)
                    }
                })
            }
        } else {
            observe(observable, {
                it?.let {
                    Cache.putString(
                        cacheKey,
                        GsonBuilder().create().toJson(it)
                    )
                    success?.invoke(it)
                }
            })
        }
    }
}