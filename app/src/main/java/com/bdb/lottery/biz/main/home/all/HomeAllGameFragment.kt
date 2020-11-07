package com.bdb.lottery.biz.main.home.all

import android.os.Bundle
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.extension.ob
import com.bdb.lottery.utils.Sizes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.single_recyclerview_layout.*

@AndroidEntryPoint
class HomeAllGameFragment : BaseFragment(R.layout.single_recyclerview_layout) {
    private val vm by viewModels<HomeAllGameViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.allGame()
    }

    override fun observe() {
        ob(vm.allgameLd.getLiveData()) {
            home_collection_rv.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                setPadding(Sizes.dp2px(4f))
                adapter = HomeAllGameAdapter(it)
            }
        }
    }
}
