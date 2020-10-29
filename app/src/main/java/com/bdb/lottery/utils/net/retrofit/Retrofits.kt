package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.extension.code
import com.bdb.lottery.extension.msg
import com.bdb.lottery.extension.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

object Retrofits {
    //rxjava 简化
    fun <Data> observe(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Any?)? = null,
        error: ((code: Int, msg: String?) -> Any?)? = null,
        complete: (() -> Any?)? = null
    ): Disposable {
        return observable.subscribeOn(Schedulers.io())
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

                    error?.run { this(it.code, it.msg) }
                    complete?.run { this() }
                },
                {
                    complete?.run { this() }
                })
    }

    //错误返回有用数据
    fun <Data> observeErrorData(
        observable: Observable<BaseResponse<Data>>,
        success: ((Data?) -> Any?)? = null,
        error: ((BaseResponse<*>) -> Any?)? = null,
        complete: (() -> Any?)? = null,
    ): Disposable {
        return observable.subscribeOn(Schedulers.io())
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
                    BdbApp.context.toast(it.msg)
                    if (it is ApiException) {
                        error?.run { this(it.response) }
                    } else {
                        error?.run { this(BaseResponse(it.code, it.msg, null)) }
                    }
                    complete?.run { this() }
                },
                {
                    complete?.run { this() }
                })
    }

}