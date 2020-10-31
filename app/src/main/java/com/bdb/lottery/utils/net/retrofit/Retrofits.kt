package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.extension.code
import com.bdb.lottery.extension.msg
import com.bdb.lottery.extension.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

object Retrofits {
    //rxjava 简化
    fun <Data> observe(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Any?)? = null,
        error: ((code: Int, msg: String?) -> Any?)? = null,
        onStart: (() -> Any?)? = null,
        complete: (() -> Any?)? = null,
        viewState: LiveDataWraper<ViewState>? = null
    ): Disposable {
        return observable
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                onStart?.invoke()
                viewState?.setData(ViewState(true))
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccess()) {
                    success?.run { this(it.data) }
                } else {
                    BdbApp.context.toast(it.msg)
                    error?.run { this(it.code, it.msg) }
                }
            },
                {
                    Timber.d("observe__onError__throwable: ${it}")
                    error?.run { this(it.code, it.msg) }
                    viewState?.setData(ViewState(false))
                    complete?.run { this() }
                },
                {
                    viewState?.setData(ViewState(false))
                    complete?.run { this() }
                })
    }

    //错误返回有用数据
    fun <Data> observeErrorData(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Any?)? = null,
        error: ((BaseResponse<*>) -> Any?)? = null,
        onStart: (() -> Any?)? = null,
        complete: (() -> Any?)? = null,
        viewState: LiveDataWraper<ViewState>? = null
    ): Disposable {
        return observable.subscribeOn(Schedulers.io())
            .doOnSubscribe {
                onStart?.invoke()
                viewState?.setData(ViewState(true))
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccess()) {
                    success?.run { this(it.data) }
                } else {
                    BdbApp.context.toast(it.msg)
                    error?.run { this(it.mappStringResponse()) }
                }
            },
                {
                    Timber.d("observe__onError__throwable: ${it}")
                    BdbApp.context.toast(it.msg)
                    if (it is ApiException) {
                        error?.run { this(it.response) }
                    } else {
                        error?.run { this(BaseResponse(it.code, it.msg, null)) }
                    }
                    viewState?.setData(ViewState(false))
                    complete?.run { this() }
                },
                {
                    viewState?.setData(ViewState(false))
                    complete?.run { this() }
                })
    }

}