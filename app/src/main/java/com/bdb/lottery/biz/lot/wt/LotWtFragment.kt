package com.bdb.lottery.biz.lot.wt

import android.os.Bundle
import android.view.View
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.lot.LotActivity
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.game.data.HomeFavoritesMapper
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.startWithArgs
import com.bdb.lottery.utils.ui.size.Sizes
import com.bdb.lottery.utils.ui.size.TSize
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview_single_layout.*
import javax.inject.Inject

@AndroidEntryPoint
class LotWtFragment : BaseFragment(R.layout.recyclerview_single_layout) {
    private val vm by viewModels<LotWtViewModel>()

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

    //region 收藏
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.getLotteryFavourites()
    }

    override fun observe() {
        ob(vm.favouritesLd.getLiveData()) {
            home_single_rv.run {
                layoutManager = GridLayoutManager(context, 2)
                setPadding(Sizes.dp2px(4f))
                adapter =
                    object : BaseQuickAdapter<HomeFavoritesMapper, BaseViewHolder>(
                        R.layout.home_common_img_item, it
                    ) {
                        override fun convert(holder: BaseViewHolder, item: HomeFavoritesMapper) {
                            Glide.with(context).load(item.homeImgUrl).placeholder(item.placeholder)
                                .into(holder.getView(R.id.home_common_img_ariv))
                        }
                    }.apply {
                        setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View, position: Int ->
                            //彩票
                            val item: HomeFavoritesMapper =
                                adapter.getItem(position) as HomeFavoritesMapper
                            val collectType = item.collectType
                            if (collectType.equalsNSpace("-1")) {
                                //收藏
                            } else {
                                //彩票、游戏
                                val gameId = item.gameId
                                val gameType = item.gameType
                                val gameName = item.gameName

                                when {
                                    collectType.equalsNSpace("0") -> item.gameInfo?.name
                                    collectType.equalsNSpace("1") -> item.thirdGameInfo?.name
                                    else -> null
                                }
                                if (null == gameId || null == gameType) {
                                    toast.showWarning("彩种异常")
                                    return@setOnItemClickListener
                                }
                                startWithArgs<LotActivity> {
                                    it.putExtra(EXTRA.ID_GAME_EXTRA, gameId)
                                    it.putExtra(EXTRA.TYPE_GAME_EXTRA, gameType)
                                    it.putExtra(EXTRA.NAME_GAME_EXTRA, gameName)
                                }
                            }
                        }
                    }
            }
        }
    }
    //endregion
}