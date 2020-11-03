package com.bdb.lottery.biz.main.promotion

import android.os.Bundle
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

//优惠
@AndroidEntryPoint
class PromotionFragment : BaseFragment(R.layout.promotion_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
    }
}