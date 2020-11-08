package com.bdb.lottery.biz.splash

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.domain.DomainRemoteDs
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
            preloadCustomServiceUrl()//缓存客服线
            preloadApkVersion()//缓存apk版本信息
        }
    }

    //获取客服
    private fun preloadCustomServiceUrl() {
        appDs.preloadCustomServiceUrl()
    }

    //获取apk版本
    private fun preloadApkVersion() {
        appDs.preloadApkVersion {
            it?.let {
                //发送粘性事件，MainActivity打开处理
                EventBus.getDefault().postSticky(it)
            }
        }
    }
}