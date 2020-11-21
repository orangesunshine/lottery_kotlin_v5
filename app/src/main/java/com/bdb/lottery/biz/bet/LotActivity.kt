package com.bdb.lottery.biz.bet

import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LotActivity : BaseActivity(R.layout.lot_activity) {

    override fun attachActionBar(): Boolean {
        return false
    }

    override fun attachStatusBar(): Boolean {
        return false
    }
}