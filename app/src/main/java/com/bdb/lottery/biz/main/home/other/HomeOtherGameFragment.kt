package com.bdb.lottery.biz.main.home.other

import androidx.fragment.app.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeOtherGameFragment : BaseFragment(R.layout.single_recyclerview_layout) {
    private val vm by viewModels<HomeOtherGameViewModel>()
}
