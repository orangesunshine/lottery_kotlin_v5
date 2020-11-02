package com.bdb.lottery.biz.splash

import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.guide.GuideActivity
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.startNdFinish
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
        resetCache()//重置cache
        splash_version_name_tv.text = Apps.getAppVersionName(this)
        showLoading()
        loadingAnim()

        id_common_content_layout.postDelayed({ vm.initDomain() }, 500)
    }

    //重启app清空cache
    private fun resetCache() {
        Cache.putString(ICache.DOMAIN_URL_CACHE, null)//域名
        Cache.putString(ICache.CUSTOM_SERVICE_URL_CACHE, null)//客服线
        Cache.putString(ICache.APK_VERSION_CACHE, null)//版本信息
        Cache.putString(ICache.PLATEFORM_CACHE, null)//平台
        Cache.putString(ICache.PUBLIC_RSA_CACHE, null)//公钥
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
        ob(vm.ldDomainRet.getLiveData()) {
            if (it) {
                //获取域名成功
                dismissLoading()
                if (Cache.getBoolean(ICache.GUIDE_CACHE)) {
                    startNdFinish<LoginActivity>()
                } else {
                    //首次进入
                    startNdFinish<GuideActivity>()
                }
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