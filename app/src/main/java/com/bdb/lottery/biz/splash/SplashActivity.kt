package com.bdb.lottery.biz.splash

import android.os.Bundle
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber

/**
 * observeOn() 只是在收到 onNext() 等消息的时候改变了从下一个开始的操作符的线程运行环境。
 * subscribeOn() 线程切换是在 subscribe() 订阅的时候切换，他会切换他下面订阅的操作符的运行环境，因为订阅的过程是自下而上的，所以第一个出现的 subscribeOn() 操作符反而是最后一次运行的。
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity(R.layout.activity_splash) {
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splash_empty_tv.setOnClickListener({
            empty()
        })

        splash_error_tv.setOnClickListener({
            error()
        })

        splash_hide_tv.setOnClickListener({
            hide()
        })

        splash_show_tv.setOnClickListener({
            show()
        })

        splash_play_tv.setOnClickListener({
//            viewModel.mock()

            Observable.fromArray(arrayOf("1", "2", "3"))
            Observable.create(ObservableOnSubscribe<String> {
                val str: String = "orange"
                it.onNext(str)
//                it.onError(NullPointerException("null"))
                Timber.d("younger__subscribe__thread__id: ${Thread.currentThread().id}, name: ${Thread.currentThread().name}")
            })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { Timber.d("younger__doOnSubscribe__thread__id: ${Thread.currentThread().id}, name: ${Thread.currentThread().name}") }
                .observeOn(Schedulers.newThread())
                .flatMap { Observable.fromArray(it + "orange") }
                .observeOn(Schedulers.computation())
                .map { it + "sunshine" }
                .observeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.d("younger__onNext__${it}__thread__id: ${Thread.currentThread().id}, name: ${Thread.currentThread().name}") },
                    { Timber.d("younger__onError__${it?.message}__thread__id: ${Thread.currentThread().id}, name: ${Thread.currentThread().name}") },
                    { Timber.d("younger__onComplete__thread__id: ${Thread.currentThread().id}, name: ${Thread.currentThread().name}") })
        })

        observe(viewModel.ldDamain.getLiveData(), {
            toast(it)
        })
    }
}