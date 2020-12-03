package com.bdb.lottery.biz.main.home

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bdb.lottery.R
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.biz.main.home.all.HomeAllGameFragment
import com.bdb.lottery.biz.main.home.collection.HomeCollectionFragment
import com.bdb.lottery.biz.main.home.other.HomeOtherGameFragment
import com.bdb.lottery.datasource.cocos.data.CocosData
import com.bdb.lottery.datasource.home.data.BannerMapper
import com.bdb.lottery.extension.bindNoFlingRecyclerView
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.start
import com.bdb.lottery.extension.unbindNoFlingRecyclerView
import com.bdb.lottery.utils.ui.activity.Activitys
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.util.BannerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_home_fragment.*
import permissions.dispatcher.*


//主页home
@RuntimePermissions
@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.main_home_fragment) {
    private val vm by viewModels<HomeViewModel>()
    val tabs = arrayOf("推荐收藏", "全部彩种", "综合娱乐")
    val fragments =
        arrayOf(HomeCollectionFragment(), HomeAllGameFragment(), HomeOtherGameFragment())

    //region viewpager2 tablayout
    private lateinit var mediator: TabLayoutMediator
    private val changeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            //可以来设置选中时tab的大小
            val tabCount: Int = homeGameTl.getTabCount()
            for (i in 0 until tabCount) {
                val tab: TabLayout.Tab? = homeGameTl.getTabAt(i)
                tab?.let {
                    val tabView = it.customView as TextView
                    val selected = it.position == position
                    tabView.textSize = if (selected) 17F else 14F
                    tabView.setTypeface(if (selected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVp()//game viewpager设置
        cocosWithPermissionCheck()
    }

    private fun initVp() {
        //禁用预加载
        homeGameVp.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT;

        //Adapter
        homeGameVp.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount(): Int {
                return tabs.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        mediator = TabLayoutMediator(
            homeGameTl,
            homeGameVp
        ) { tab: TabLayout.Tab, position: Int ->
            //这里可以自定义TabView
            val tabView = TextView(context)
            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()
            val colors = intArrayOf(
                ContextCompat.getColor(BdbApp.context, R.color.color_333333),
                ContextCompat.getColor(
                    BdbApp.context,
                    R.color.color_999999
                )
            )
            val colorStateList = ColorStateList(states, colors)
            tabView.text = tabs[position]
            tabView.textSize = 14F
            tabView.setTextColor(colorStateList)
            tabView.gravity = Gravity.CENTER_HORIZONTAL
            tab.customView = tabView
            tab.view.background = null//默认点击效果去掉
        }
        //要执行这一句才是真正将两者绑定起来
        mediator.attach()
        homeGameVp.registerOnPageChangeCallback(changeCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeGameVp?.unregisterOnPageChangeCallback(changeCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediator.detach();
    }
    //endregion

    //region smartrefreshlayout nofling
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (fragments[0] as HomeCollectionFragment).bindNoFling {
            home_refreshLayout.bindNoFlingRecyclerView(
                it
            )
        }

        (fragments[0] as HomeCollectionFragment).unbindNoFling {
            home_refreshLayout.unbindNoFlingRecyclerView(
                it
            )
        }

        (fragments[1] as HomeAllGameFragment).bindNoFling {
            home_refreshLayout.bindNoFlingRecyclerView(
                it
            )
        }

        (fragments[1] as HomeAllGameFragment).unbindNoFling {
            home_refreshLayout.unbindNoFlingRecyclerView(
                it
            )
        }

        (fragments[2] as HomeOtherGameFragment).bindNoFling {
            home_refreshLayout.bindNoFlingRecyclerView(
                it
            )
        }

        (fragments[2] as HomeOtherGameFragment).unbindNoFling {
            home_refreshLayout.unbindNoFlingRecyclerView(
                it
            )
        }
    }
    //endregion

    //region 轮播图，通知、余额、预加载（收藏、全部game、娱乐）
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.noticeData()//公告
        vm.bannerData()//轮播图
        vm.getBalance()//获取余额
        vm.preload()//预加载 收藏、全部game、娱乐
    }

    override fun observe() {
        ob(vm.balanceLd.getLiveData()) { home_balance_tv.text = it }
        ob(vm.bannerLd.getLiveData()) {
            loadBanner(it)
        }
        ob(vm.noticeLd.getLiveData()) {
            home_notice_tv.text = it
        }
    }

    private fun loadBanner(list: List<BannerMapper>?) {
        home_banner?.run {
            setBannerGalleryEffect(18, 10)
            indicator = RectangleIndicator(context)
            setIndicatorSelectedWidth(BannerUtils.dp2px(20f).toInt())
            setIndicatorSpace(BannerUtils.dp2px(4f).toInt())
            setIndicatorRadius(0)
            addBannerLifecycleObserver(this@HomeFragment)
            adapter = object : BannerImageAdapter<BannerMapper>(list) {
                override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerImageHolder? {
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
            setOnBannerListener(object : OnBannerListener<BannerMapper> {
                override fun OnBannerClick(data: BannerMapper, position: Int) {
                    if (data.needJump) start<LoginActivity>()
                }
            })
        }
    }
    //endregion

    //region sd卡权限，cocos下载
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun cocos() {
        vm.downloadAllCocosFiles()//下载全部cocos文件
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    //给用户解释要请求什么权限，为什么需要此权限
    fun showSingleRationale(request: PermissionRequest) {
        val activity = mActivity?.get()
        if (Activitys.isActivityAlive(activity)) {
            AlertDialog.Builder(activity!!)
                .setMessage("拒绝将影响应用的正常使用，请允许")
                .setPositiveButton("允许") { dialog, which ->
                    request.proceed() //继续执行请求
                }.setNegativeButton("拒绝") { dialog, which ->
                    request.cancel() //取消执行请求
                }.show()
        }
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE) //一旦用户拒绝了
    fun storageDenied() {
        toast.showWarning("SD卡权限已拒绝")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
    //endregion
}