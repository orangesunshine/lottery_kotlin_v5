package com.bdb.lottery.biz.main.home.other

import android.os.Bundle
import android.view.View
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.extension.ob
import com.bdb.lottery.utils.ui.TSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.single_recyclerview_layout.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeOtherGameFragment : BaseFragment(R.layout.single_recyclerview_layout) {
    private val vm by viewModels<HomeOtherGameViewModel>()
    @Inject
    lateinit var tSize: TSize

    //region fling 惯性处理
    private var bindNoFling: ((RecyclerView) -> Unit)? = null
    private var unbindNoFling: ((RecyclerView) -> Unit)? = null

    fun bindNoFling(noFling: (RecyclerView) -> Unit) {
        this.bindNoFling = noFling
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindNoFling?.invoke(home_single_rv)
    }

    fun unbindNoFling(noFling: (RecyclerView) -> Unit) {
        this.unbindNoFling = noFling
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindNoFling?.invoke(home_single_rv)
    }
    //endregion

    //region 全部game
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.otherGame()
    }

    override fun observe() {
        ob(vm.otherGameLd.getLiveData()) {
            home_single_rv.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                setPadding(tSize.dp2px(4f))
                adapter = HomeOtherGameAdapter(it)
            }
        }
    }
    //endregion
}
