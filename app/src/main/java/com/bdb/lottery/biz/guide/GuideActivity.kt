package com.bdb.lottery.biz.guide

import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.main.MainActivity
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.alphaVisible
import com.bdb.lottery.extension.startNdFinish
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.ui.TApp
import com.bdb.lottery.utils.cache.TCache
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.guide_activity.*
import javax.inject.Inject


@AndroidEntryPoint
class GuideActivity : BaseActivity(R.layout.guide_activity) {
    @Inject
    lateinit var tApp: TApp
    @Inject
    lateinit var tCache: TCache

    override fun initVar() {
        super.initVar()
        statusbarLight = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guideDotFirstView.isSelected = true
        val imgs = mutableListOf<Int>().apply {
            if (!tApp.isLBH()) {
                add(R.drawable.guide_first_bg);
            } else {
                guideDotThirdView.visible(false)
            }
            add(R.drawable.guide_second_bg)
            add(R.drawable.guide_third_bg)
        }

        //去登录
        guideTologinTv.setOnClickListener {
            tCache.putBoolean(ICache.GUIDE_CACHE, true)
            startNdFinish<MainActivity>()
        }

        //guide页
        guideBannerVp2.run {
            adapter =
                object :
                    BaseQuickAdapter<Int, BaseViewHolder>(R.layout.img_single_item, imgs) {
                    override fun convert(holder: BaseViewHolder, item: Int) {
                        holder.getView<ImageView>(R.id.img_common_iv).scaleType = ImageView.ScaleType.FIT_START
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
                    guideDotFirstView.isSelected = false
                    guideDotSecondView.isSelected = false
                    guideDotThirdView.isSelected = false
                    when (position) {
                        0 -> guideDotFirstView.isSelected = true
                        1 -> guideDotSecondView.isSelected = true
                        2 -> guideDotThirdView.isSelected = true
                    }
                }
            })
        }

    }

    fun showTologinBt() {
        guideDotLl.alphaVisible(false)
        guideTologinTv.alphaVisible(true)
    }

    fun dimissTologinBt() {
        guideDotLl.alphaVisible(true)
        guideTologinTv.alphaVisible(false)
    }

    override fun attachStatusBar(): Boolean {
        return false
    }

    override fun attachActionBar(): Boolean {
        return false
    }
}