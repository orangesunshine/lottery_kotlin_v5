package com.bdb.lottery.biz.splash

import android.os.Bundle
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.extension.toast
import com.bdb.lottery.utils.Devices
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast("deviceId: ${Devices.getDeviceUUid()}")
    }
}