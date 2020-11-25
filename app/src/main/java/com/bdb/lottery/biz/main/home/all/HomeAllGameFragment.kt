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
import com.bdb.lottery.const.IExtra
import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.startWithArgs
import com.bdb.lottery.utils.ui.TActivity
import com.bdb.lottery.utils.ui.TSize
import com.chad.library.adapter.base.BaseQuickAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview_single_layout.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeAllGameFragment : BaseFragment(R.layout.recyclerview_single_layout) {
    private val vm by viewModels<HomeAllGameViewModel>()

    @Inject
    lateinit var tSize: TSize
    @Inject
    lateinit var tActivity: TActivity

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
                setPadding(tSize.dp2px(4f))
                adapter = HomeAllGameAdapter(it).apply {
                    setOnSubItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View, position: Int ->
                        val item: AllGameItemData =
                            adapter.getItem(position) as AllGameItemData

                        //彩票、游戏
                        val gameId = item.gameId
                        val gameType = item.gameType
                        val gameName = item.name

                        if (null == gameId || null == gameType) {
                            toast.showWarning("彩种异常")
                            return@setOnSubItemClickListener
                        }

                        startWithArgs<LotActivity> {
                            it.putExtra(IExtra.ID_GAME_EXTRA, gameId.toString())
                            it.putExtra(IExtra.TYPE_GAME_EXTRA, gameType.toString())
                            if (!gameName.isSpace())
                                it.putExtra(IExtra.NAME_GAME_EXTRA, gameName)
                        }
                    }
                }
            }
        }
    }
    //endregion
}
