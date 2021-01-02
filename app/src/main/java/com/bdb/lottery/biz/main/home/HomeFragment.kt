package com.bdb.lottery.biz.main.home

import android.Manifest
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.account.AccountManager
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.biz.main.home.all.HomeAllGameFragment
import com.bdb.lottery.biz.main.home.collection.HomeCollectionFragment
import com.bdb.lottery.biz.main.home.other.HomeOtherGameFragment
import com.bdb.lottery.datasource.home.data.BannerMapper
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.adapterPattern.OnTabSelectedListenerAdapter
import com.bdb.lottery.utils.ui.activity.Activitys
import com.bdb.lottery.utils.ui.view.Views
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
import javax.inject.Inject


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVp()//game viewpager设置
        cocosDownloadWithPermissionCheck()
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
            tabView.textSize = 14F
            tabView.text = tabs[position]
            tabView.gravity = Gravity.CENTER_HORIZONTAL
            tab.customView = tabView
//            tab.view.background = null//默认点击效果去掉
        }
        //要执行这一句才是真正将两者绑定起来
        mediator.attach()
        homeGameTl.addOnTabSelectedListener(object : OnTabSelectedListenerAdapter() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab.updateTab(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab.updateTab(false)
            }
        })
        homeGameTl.getTabAt(0).updateTab(true)//选中第一个
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

    @Inject
    lateinit var accountManager: AccountManager
    override fun observe() {
        ob(accountManager.mUserBalance.getLiveData()) {
            home_balance_tv.text = (it?.center ?: 0.0).money()
        }
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
    fun cocosDownload() {
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