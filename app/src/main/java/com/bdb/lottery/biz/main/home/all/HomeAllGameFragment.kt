package com.bdb.lottery.biz.main.home.all

import android.os.Bundle
import android.view.View
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.lot.LotActivity
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.startWithArgs
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.BaseQuickAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview_single_layout.*

@AndroidEntryPoint
class HomeAllGameFragment : BaseFragment(R.layout.recyclerview_single_layout) {
    private val vm by viewModels<HomeAllGameViewModel>()

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
        vm.allGame()
    }

    override fun observe() {
        ob(vm.allGameLd.getLiveData()) {
            home_single_rv.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                setPadding(Sizes.dp2px(4f))
                adapter = HomeAllGameAdapter(it).apply {
                    setOnSubItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View, position: Int ->
                        val itemOrNull = adapter.getItemOrNull(position)

                        val item: AllGameItemData? =
                            itemOrNull?.let { if (it is AllGameItemData) it else null }
                        //彩票
                        var jdEnable: Boolean? = false
                        var trEnable: Boolean? = false
                        var wtEnable: Boolean? = false
                        var gameId: Int? = null
                        var gameType: Int? = null
                        var gameName: String? = null
                        if (null != item) {
                            jdEnable = item.ptEnable
                            trEnable = item.kgEnable
                            wtEnable = item.wtEnable
                            gameId = item.gameId
                            gameType = item.gameType
                            gameName = item.name
                        }

                        if (null == gameId || null == gameType) {
                            toast.showWarning("彩种异常")
                            return@setOnSubItemClickListener
                        }

                        startWithArgs<LotActivity> {
                            it.putExtra(EXTRA.ID_GAME_EXTRA, gameId)
                            it.putExtra(EXTRA.TYPE_GAME_EXTRA, gameType)
                            it.putExtra(EXTRA.NAME_GAME_EXTRA, gameName)
                            it.putExtra(EXTRA.JD_ENABLE_GAME_EXTRA, jdEnable)
                            it.putExtra(EXTRA.TR_ENABLE_GAME_EXTRA, trEnable)
                            it.putExtra(EXTRA.WT_ENABLE_GAME_EXTRA, wtEnable)
                        }
                    }
                }
            }
        }
    }
    //endregion
}
