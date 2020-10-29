package com.bdb.lottery.biz.splash

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.animation.AlphaAnimation
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.guide.GuideActivity
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.start
import com.bdb.lottery.extension.toast
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.Apps
import com.bdb.lottery.utils.cache.Cache
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.splash_activity.*

@AndroidEntryPoint
class SplashActivity : BaseActivity(R.layout.splash_activity) {
    private val vm by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        version_name_tv.text = Apps.getAppVersionName(this)
        showLoading()
        loadingAnim()

        id_common_content_layout.postDelayed({vm.initDomain()},1000)
    }

    private fun loadingAnim() {
        val alpha = AlphaAnimation(0.5f, 1f)
        alpha.fillAfter = true
        alpha.duration = 1000
        content?.startAnimation(alpha)
    }

    fun showLoading() {
        splash_loading_lav.visible(true)
        splash_loading_lav.playAnimation()
    }

    fun dismissLoading() {
        splash_loading_lav.visible(false)
    }

    override fun observe() {
        observe(vm.ldDomainRet.getLiveData()) {
            if (null != it && it) {
                //获取域名成功
                dismissLoading()
                if (Cache.getBoolean(ICache.GUIDE_CACHE)) {
                    start<LoginActivity>()
                } else {
                    //首次进入
                    start<GuideActivity>()
                }
                finish()
            } else {
                toast("获取域名失败")
            }
        }
    }

    override fun onBack() {
        Apps.killApp()
    }

    override fun attachStatusBar(): Boolean {
        return false
    }

    override fun attachActionBar(): Boolean {
        return false
    }
}