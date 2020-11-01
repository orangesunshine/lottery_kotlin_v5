package com.bdb.lottery.biz.main.home

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.extension.ob
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*

//彩票大厅
@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.home_fragment) {
    private val vm by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getBalance()//获取余额
    }

    override fun observe() {
        ob(vm.balanceLd.getLiveData()) { home_balance_tv.text = it }
    }
}