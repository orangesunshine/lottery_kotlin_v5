package com.bdb.lottery.biz.register

import android.os.Bundle
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.register_activity.*

@AndroidEntryPoint
class RegisterActivity : BaseActivity(R.layout.register_activity) {
    private val vm by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        register_bt.setOnClickListener {
            vm.register()
        }
    }
}