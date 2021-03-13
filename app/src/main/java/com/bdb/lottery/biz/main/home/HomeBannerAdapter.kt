package com.bdb.lottery.biz.main.home

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.bdb.lottery.R
import com.bdb.lottery.datasource.home.data.BannerMapper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder

class HomeBannerAdapter(private val context: Context, mData: List<BannerMapper>?) :
    BannerImageAdapter<BannerMapper>(mData) {
    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerImageHolder {
        val imageView = ImageView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params
        imageView.adjustViewBounds = true
        return BannerImageHolder(imageView)
    }

    override fun onBindView(
        holder: BannerImageHolder?,
        data: BannerMapper?,
        position: Int,
        size: Int,
    ) {
        Glide.with(holder!!.itemView)
            .load(data?.imgurl)
            .thumbnail(
                Glide.with(holder.itemView)
                    .load(R.drawable.placeholder_square_picture_bg)
            )
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(holder.imageView)
    }
}