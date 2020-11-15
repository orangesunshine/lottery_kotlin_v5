package com.bdb.lottery.biz.splash

import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.guide.GuideActivity
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.const.IUrl
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.startNdFinish
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.ui.TApp
import com.bdb.lottery.utils.cache.TCache
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.splash_activity.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity(R.layout.splash_activity) {
    private val vm by viewModels<SplashViewModel>()
    @Inject
    lateinit var tApp: TApp
    @Inject
    lateinit var tCache: TCache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetCache()//重置cache
        splash_version_name_tv.text = tApp.getAppVersionName()
        showLoading()
        loadingAnim()

        id_common_content_layout.postDelayed({ vm.initDomain() }, 500)
    }

    //重启app清空cache
    private fun resetCache() {
        tCache.putString(ICache.DOMAIN_URL_CACHE)//域名
        tCache.putString(IUrl.URL_PLATFORM_PARAMS)//平台
        tCache.putString(ICache.PUBLIC_RSA_KEY_CACHE)//公钥
        tCache.putString(IUrl.URL_CUSTOM_SERVICE)//客服线
        tCache.putString(IUrl.URL_APK_VERSION)//版本信息
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
                if (tCache.getBoolean(ICache.GUIDE_CACHE)) {
                    startNdFinish<LoginActivity>()
                } else {
                    //首次进入
                    startNdFinish<GuideActivity>()
                }
            }
        }
    }

    override fun onBack() {
        tApp.killApp()
    }

    override fun attachStatusBar(): Boolean {
        return false
    }

    override fun attachActionBar(): Boolean {
        return false
    }
}