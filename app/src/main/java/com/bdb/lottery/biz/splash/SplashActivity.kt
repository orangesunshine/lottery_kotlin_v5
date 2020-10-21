package com.bdb.lottery.biz.splash

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bdb.lottery.BuildConfig
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*

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
            viewModel.mock()
        })

        observe(viewModel.ldDamain.getLiveData(),{
            toast(it)
        })
    }
}