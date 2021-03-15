package com.bdb.lottery.biz.main.home.collection

import android.os.Bundle
import android.view.View
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.lot.activity.LotActivity
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
class HomeCollectionFragment : BaseFragment(R.layout.recyclerview_single_layout) {
    private val vm by viewModels<HomeCollectionViewModel>()

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

    //region 收藏数据请求、结果处理
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
                            } else if (collectType.equalsNSpace("0")) {
                                //彩票
                                var jdEnable: Boolean? = false
                                var trEnable: Boolean? = false
                                var wtEnable: Boolean? = false
                                val gameId = item.gameId
                                val gameType = item.gameType
                                val gameName = item.gameName
                                val gameInfo = item.gameInfo
                                if (null != gameInfo) {
                                    jdEnable = gameInfo.ptEnable
                                    trEnable = gameInfo.kgEnable
                                    wtEnable = gameInfo.wtEnable
                                }

                                if (null == gameId || null == gameType) {
                                    toast.showWarning("彩种异常")
                                    return@setOnItemClickListener
                                }

                                startWithArgs<LotActivity> {
                                    it.putExtra(EXTRA.ID_GAME_EXTRA, gameId)
                                    it.putExtra(EXTRA.TYPE_GAME_EXTRA, gameType)
                                    it.putExtra(EXTRA.NAME_GAME_EXTRA, gameName)
                                    it.putExtra(EXTRA.JD_ENABLE_GAME_EXTRA, jdEnable)
                                    it.putExtra(EXTRA.TR_ENABLE_GAME_EXTRA, trEnable)
                                    it.putExtra(EXTRA.WT_ENABLE_GAME_EXTRA, wtEnable)
                                }
                            } else {
                                //游戏
                            }
                        }
                    }
            }
        }
    }
    //endregion
}