package com.bdb.lottery.biz.main.home.other

import com.bdb.lottery.R
import com.bdb.lottery.datasource.game.data.OtherPlatformMIR
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class HomeOtherGameItemAdapter(data: MutableList<OtherPlatformMIR>?) :
    BaseQuickAdapter<OtherPlatformMIR, BaseViewHolder>(
        R.layout.home_allgame_gametype_item,
        data
    ) {
    override fun convert(holder: BaseViewHolder, item: OtherPlatformMIR) {
        item?.let {
            holder.run {
                setText(R.id.home_allgame_gametype_item_name_tv, it.name)//彩种名称

                Glide.with(context).load(item.imgUrl)
                    .placeholder(R.drawable.home_placeholder_circle_img_ic)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(holder.getView(R.id.home_allgame_gametype_item_iv))
            }
        }
    }

}