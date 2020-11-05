package com.bdb.lottery.biz.main.home.all

import android.widget.ImageView
import com.bdb.lottery.R
import com.bdb.lottery.datasource.game.data.AllGameDataItemData
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class HomeAllGameItemAdapter(datas: MutableList<AllGameDataItemData>?) :
    BaseQuickAdapter<AllGameDataItemData, BaseViewHolder>(R.layout.img_item_common_layout, datas) {
    override fun convert(holder: BaseViewHolder, item: AllGameDataItemData) {
        holder.getView<ImageView>(R.id.img_common_iv)
    }

}