package com.bdb.lottery.biz.main.home.all

import com.bdb.lottery.R
import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class HomeAllGameSubAdapter(data: MutableList<AllGameItemData>?) :
    BaseQuickAdapter<AllGameItemData, BaseViewHolder>(
        R.layout.home_allgame_item,
        data
    ) {
    override fun convert(holder: BaseViewHolder, item: AllGameItemData) {
        item.let {
            holder.run {
                setText(R.id.homeAllGameNameTv, it.name)//彩种名称
                //频率
                item.remark?.split("\\|")?.let {
                    if (it.isEmpty()) setText(
                        R.id.homeAllGameFrequencyTimeTv,
                        it[0]
                    )
                }

                //热、新、休市
                val recommend = when (it.recommendType) {
                    "H" -> R.drawable.home_allgame_recommend_hot
                    "N" -> R.drawable.home_allgame_recommend_new
                    "s" -> R.drawable.home_allgame_recommend_closed
                    else -> 0
                }
                if (recommend > 0) holder.setImageResource(
                    R.id.homeAllGameRecommendIv,
                    recommend
                )

                Glide.with(context).load(item.imgUrl)
                    .placeholder(R.drawable.home_placeholder_circle_img_ic)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(holder.getView(R.id.homeAllGameItemIv))
            }
        }
    }

}