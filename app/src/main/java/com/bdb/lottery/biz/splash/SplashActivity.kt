package com.bdb.lottery.biz.splash

import android.os.Bundle
import android.view.View
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*

@AndroidEntryPoint
class SplashActivity : BaseActivity(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splash_empty_tv.setOnClickListener(View.OnClickListener {
            empty()
        })

        splash_error_tv.setOnClickListener(View.OnClickListener {
            error()
        })

        splash_hide_tv.setOnClickListener(View.OnClickListener {
            hide()
        })

        splash_show_tv.setOnClickListener(View.OnClickListener {
            show()
        })

    }
}