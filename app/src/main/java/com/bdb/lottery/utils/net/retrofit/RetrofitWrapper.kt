package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.datasource.domain.DomainRemoteDs
import com.bdb.lottery.extension.code
import com.bdb.lottery.extension.msg
import com.bdb.lottery.extension.toast
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@ActivityRetainedScoped
class RetrofitWrapper @Inject constructor(
    private val domainLocalDs: DomainLocalDs,
    private val domainRemoteDs: DomainRemoteDs
) {
    //rxjava 简化
    fun <Data> observe(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Unit)? = null,
        error: ((code: Int, msg: String?) -> Unit)? = null,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
        viewState: LiveDataWraper<ViewState?>? = null
    ) {
        domainRemoteDs.getDomain {
            observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    onStart?.invoke(it)
                    viewState?.setData(ViewState(true))
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    success?.run { this(it.data) }
                },
                    {
                        Timber.d("observe__onError__throwable: ${it}")
                        val code = it.code
                        val msg = it.msg
                        BdbApp.context.toast(msg)
                        error?.run { this(code, msg) }
                        if (code >= 500) domainLocalDs.clearDomain()
                        viewState?.setData(ViewState(false))
                        complete?.run { this() }
                    },
                    {
                        viewState?.setData(ViewState(false))
                        complete?.run { this() }
                    })
        }
    }

    //错误返回有用数据
    fun <Data> observeErrorData(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Unit)? = null,
        error: ((BaseResponse<*>) -> Unit)? = null,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
        viewState: LiveDataWraper<ViewState?>? = null
    ) {
        domainRemoteDs.getDomain {
            observable.subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    onStart?.invoke(it)
                    viewState?.setData(ViewState(true))
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    success?.run { this(it.data) }
                },
                    {
                        Timber.d("observe__onError__throwable: ${it}")
                        BdbApp.context.toast(it.msg)
                        val code = it.code
                        val msg = it.msg
                        if (it is ApiException) {
                            error?.run { this(it.response) }
                        } else {
                            error?.run { this(BaseResponse(code, msg, null)) }
                        }
                        if (code >= 500) domainLocalDs.clearDomain()
                        viewState?.setData(ViewState(false))
                        complete?.run { this() }
                    },
                    {
                        viewState?.setData(ViewState(false))
                        complete?.run { this() }
                    })
        }
    }

}