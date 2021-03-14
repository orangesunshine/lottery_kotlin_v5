package com.bdb.lottery.biz.splash

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.domain.DomainRemoteDs
import com.bdb.lottery.service.ServiceManager
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SplashViewModel @ViewModelInject @Inject constructor(
    private val remoteDomainDs: DomainRemoteDs,
    private val appDs: AppRemoteDs
) : BaseViewModel() {

    init {
        ServiceManager.stopAll()
    }

    //region 初始化域名：成功后继续：1.缓存客服线路，2.缓存apk版本信息
    val ldDomainRet = LiveDataWrapper<Boolean>()
    fun initDomain() {
        remoteDomainDs.getDomain {
            //获取域名成功
            ldDomainRet.setData(true)
            preCustomServiceUrl()//缓存客服线
            preApkVersion()//缓存apk版本信息
        }
    }
    //endregion

    //region 预加载客服线路
    private fun preCustomServiceUrl() {
        appDs.preCacheCustomServiceUrl()
    }
    //endregion

    //region 预加载版本信息
    private fun preApkVersion() {
        appDs.preCacheApkVersion {
            it?.let {
                //发送粘性事件，MainActivity打开处理
                EventBus.getDefault().postSticky(it)
            }
        }
    }
    //endregion
}