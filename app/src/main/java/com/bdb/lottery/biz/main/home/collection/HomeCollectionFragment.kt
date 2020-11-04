package com.bdb.lottery.biz.main.home.collection

import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.datasource.game.data.HomeFavoritesMapper
import com.bdb.lottery.extension.ob
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_collection_fragment.*

@AndroidEntryPoint
class HomeCollectionFragment : BaseFragment(R.layout.home_collection_fragment) {
    private val vm by viewModels<HomeCollectionViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.getLotteryFavourites()
    }

    override fun observe() {
        ob(vm.favouritesLd.getLiveData()) {
            home_collection_rv.run {
                adapter =
                    object : BaseQuickAdapter<HomeFavoritesMapper, BaseViewHolder>(
                        R.layout.home_common_img_item, it
                    ) {
                        override fun convert(holder: BaseViewHolder, item: HomeFavoritesMapper) {
                            Glide.with(context).load(item.homeImgUrl).placeholder(item.placeholder)
                                .into(holder.getView(R.id.home_common_img_ariv))
                        }
                    }.apply {
                        setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
                            if (position == adapter.itemCount) {
                                //收藏
                            } else {
                                //彩票
                            }
                        }
                    }
            }
        }
    }
}