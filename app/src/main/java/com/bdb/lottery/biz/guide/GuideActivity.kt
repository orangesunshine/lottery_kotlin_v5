package com.bdb.lottery.biz.guide

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.main.MainActivity
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.alphaVisible
import com.bdb.lottery.extension.start
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.cache.Cache
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.guide_activity.*


class GuideActivity : BaseActivity(R.layout.guide_activity, false) {
    override fun initVar() {
        super.initVar()
        statusbarLight = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guide_dot_first_v.isSelected = true
        val imgs = mutableListOf<Int>().apply {
            if (!Apps.isLBH(this@GuideActivity)) {
                add(R.drawable.guide_first_bg);
            } else {
                guide_dot_third_v.visible(false)
            }
            add(R.drawable.guide_second_bg)
            add(R.drawable.guide_third_bg)
        }

        //去登录
        guide_tologin_tv.setOnClickListener({
            Cache.putBoolean(ICache.CACHE_GUIDE, true)
            start<MainActivity>()
            finish()
        })

        //guide页
        guide_banner_vp2.apply {
            adapter =
                object :
                    BaseQuickAdapter<Int, BaseViewHolder>(R.layout.img_common_item_layout, imgs) {
                    override fun convert(holder: BaseViewHolder, item: Int) {
                        holder.setImageResource(R.id.img_common_iv, item)
                    }
                }
            var prePosition = 0
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    if (2 == position) {
                        showTologinBt()
                    } else if (1 == position && prePosition == 2) {
                        dimissTologinBt()
                    }
                    prePosition = position

                    //dot
                    guide_dot_first_v.isSelected = false
                    guide_dot_second_v.isSelected = false
                    guide_dot_third_v.isSelected = false
                    when (position) {
                        0 -> guide_dot_first_v.isSelected = true
                        1 -> guide_dot_second_v.isSelected = true
                        2 -> guide_dot_third_v.isSelected = true
                    }
                }
            })
        }

    }

    fun showTologinBt() {
        guide_dot_ll.alphaVisible(false)
        guide_tologin_tv.alphaVisible(true)
    }

    fun dimissTologinBt() {
        guide_dot_ll.alphaVisible(true)
        guide_tologin_tv.alphaVisible(false)
    }
}