package com.bdb.lottery.biz.splash

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.domain.DomainRemoteDs
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.start
import com.bdb.lottery.utils.cache.Cache
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ActivityContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SplashViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    private val remoteDomainDs: DomainRemoteDs,
    private val appDs: AppRemoteDs
) : BaseViewModel() {
    val ldDomainRet = LiveDataWraper<Boolean>()

    //初始化域名
    fun initDomain() {
        remoteDomainDs.getDomain {
            //获取域名成功
            ldDomainRet.setData(true)
            getCustomService()
            getApkVersion()
        }
    }

    //获取客服
    fun getCustomService() {
        appDs.getCustomServiceUrl {
            it?.let {
                if (it.kefuxian.isSpace())
                    Cache.putString(ICache.CUSTOM_SERVICE_URL_CACHE, it.kefuxian)
            }
        }
    }

    //获取apk版本
    fun getApkVersion() {
        appDs.getAPkVeresion {
            it?.let {
                //缓存apk版本信息
                Cache.putString(ICache.APK_VERSION_CACHE, Gson().toJson(it))
                //发送粘性事件，MainActivity打开处理
                EventBus.getDefault().postSticky(it)
            }
        }
    }
}