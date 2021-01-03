package com.bdb.lottery.biz.splash

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.domain.DomainRemoteDs
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SplashViewModel @ViewModelInject @Inject constructor(
    private val remoteDomainDs: DomainRemoteDs,
    private val appDs: AppRemoteDs
) : BaseViewModel() {
    val ldDomainRet = LiveDataWrapper<Boolean>()

    //初始化域名
    fun initDomain() {
        remoteDomainDs.getDomain {
            //获取域名成功
            ldDomainRet.setData(true)
            preCustomServiceUrl()//缓存客服线
            preApkVersion()//缓存apk版本信息
        }
    }

    //获取客服
    private fun preCustomServiceUrl() {
        appDs.preCustomServiceUrl()
    }

    //获取apk版本
    private fun preApkVersion() {
        appDs.refreshApkVersionCache {
            it?.let {
                //发送粘性事件，MainActivity打开处理
                EventBus.getDefault().postSticky(it)
            }
        }
    }
}