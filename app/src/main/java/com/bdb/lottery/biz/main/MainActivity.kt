package com.bdb.lottery.biz.main

import android.os.Bundle
import com.bdb.lottery.R
import com.bdb.lottery.base.contract.IContract
import com.bdb.lottery.base.ui.BaseActivity


class MainActivity : BaseActivity<IContract.IPresenter<IContract.IView>>() {
    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println()
    }
}