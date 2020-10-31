package com.bdb.lottery.biz.main.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*

//彩票大厅
@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.home_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        home_show.setOnClickListener { show() }
        home_error.setOnClickListener { loadingLayout?.showError() }
        home_empty.setOnClickListener { loadingLayout?.showEmpty() }
    }

    override fun emptyErrorRoot(): ViewGroup? {
        return home_loadinglayout_fl
    }
}