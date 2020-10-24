package com.bdb.lottery.biz.splash

import android.os.Bundle
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.LoadingDialog
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

/**
 * observeOn() 只是在收到 onNext() 等消息的时候改变了从下一个开始的操作符的线程运行环境。
 * subscribeOn() 线程切换是在 subscribe() 订阅的时候切换，他会切换他下面订阅的操作符的运行环境，因为订阅的过程是自下而上的，所以第一个出现的 subscribeOn() 操作符反而是最后一次运行的。
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity(R.layout.activity_splash) {
    private val splashViewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashViewModel.initDomain()
        observe(splashViewModel.ldDialogOnDomainError.getLiveData(),{toast("获取域名失败")})
    }
}