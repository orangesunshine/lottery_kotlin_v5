package com.bdb.lottery.biz.main.home

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.datasource.home.data.BannerMapper
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.start
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sunfusheng.marqueeview.MarqueeView
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.util.BannerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*

//彩票大厅
@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.home_fragment) {
    private val vm by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.bannerData()//轮播图
        vm.getBalance()//获取余额
    }

    override fun observe() {
        ob(vm.balanceLd.getLiveData()) { home_balance_tv.text = it }
        ob(vm.bannerLd.getLiveData()) {
            val list: List<BannerMapper> = it ?: listOf()
            loadBanner(list)
        }
        ob(vm.noticeLd.getLiveData()) {
            (home_notice_mqv as MarqueeView<String>).startWithList(it)
        }
    }

    fun loadBanner(list: List<BannerMapper>) {
        home_banner?.run {
            setBannerGalleryEffect(18, 10)
            setIndicator(RectangleIndicator(context))
            setIndicatorSelectedWidth(BannerUtils.dp2px(20f).toInt())
            setIndicatorSpace(BannerUtils.dp2px(4f).toInt())
            setIndicatorRadius(0)
            addBannerLifecycleObserver(this@HomeFragment)
            adapter = object : BannerImageAdapter<BannerMapper>(list) {
                override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerImageHolder? {
                    val imageView = ImageView(parent!!.context)
                    val params = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    imageView.layoutParams = params
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    //通过裁剪实现圆角
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BannerUtils.setBannerRound(imageView, 20f)
                    }
                    return BannerImageHolder(imageView)
                }

                override fun onBindView(
                    holder: BannerImageHolder?,
                    data: BannerMapper?,
                    position: Int,
                    size: Int
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
            setOnBannerListener(object : OnBannerListener<BannerMapper> {
                override fun OnBannerClick(data: BannerMapper, position: Int) {
                    if (data.needJump) start<LoginActivity>()
                }
            })
        }
    }
}