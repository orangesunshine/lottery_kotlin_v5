package com.bdb.lottery.biz.splash

import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.globallivedata.AccountManager
import com.bdb.lottery.biz.guide.GuideActivity
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.biz.main.MainActivity
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.startNdFinish
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.timber.TPeriod
import com.bdb.lottery.utils.ui.app.Apps
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.splash_activity.*
import javax.inject.Inject

/*
 *  页面：启动页
 *  关键字：splash
 *  说明：启动页面
 *  跳转：
 *      进：  点击桌面应用图标
 *      出：  首次进入--引导页
 *              未登录--登录页
 *              登录--main
 *  日期：2021/3/13
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashViewModel>(R.layout.splash_activity) {

    @Inject
    lateinit var tCache: TCache

    @Inject
    lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tCache.clearCacheOnSplash()//重置cache
        splashVersionTv.text = Apps.getAppVersionName()//版本号
        showSplashLoading()
        loadingAnim()

        content_layout_id.postDelayed({ vm.initDomain() }, 500)
    }

    override fun observe() {
        ob(vm.ldDomainRet.getLiveData()) {
            if (it) {
                //获取域名成功
                dismissSplashLoading()
                if (tCache.splashGuideCache() == true) {
                    if (accountManager.isLogin()) {
                        startNdFinish<MainActivity>()
                    } else {
                        startNdFinish<LoginActivity>()
                    }

                } else {
                    //首次进入
                    tCache.cacheSplashGuide()
                    startNdFinish<GuideActivity>()
                }
            }
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        Apps.killApp()
//    }

    //region 状态栏、标题栏
    override fun attachStatusBar(): Boolean {
        return false
    }

    override fun attachActionBar(): Boolean {
        return false
    }
    //endregion

    //region 启动图开场动效
    private fun loadingAnim() {
        val alpha = AlphaAnimation(0.5f, 1f)
        alpha.fillAfter = true
        alpha.duration = 1000
        content?.startAnimation(alpha)
    }
    //endregion

    //region 获取域名loading
    private fun showSplashLoading() {
        splashLoadingLav.visible(true)
        splashLoadingLav.playAnimation()
    }

    private fun dismissSplashLoading() {
        splashLoadingLav.visible(false)
    }
    //endregion
}