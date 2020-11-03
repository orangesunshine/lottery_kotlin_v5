package com.bdb.lottery.datasource.img

import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.datasource.app.AppApi
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import javax.inject.Inject

class ImgRemoteDs @Inject constructor(
    private val domainLocalDs: DomainLocalDs,
    private val imgLocalDs: ImgLocalDs,
    val retrofitWrapper: RetrofitWrapper,
    private val appApi: AppApi
) {
    //图片服务器
    fun platformParams(paramsSuccess: (String) -> Unit) {
        val imgHost = imgLocalDs.getImgDomain()
        if (!imgHost.isSpace()) {
            paramsSuccess(imgHost!!)
        } else {
            retrofitWrapper.observe(
                appApi.plateformParams(domainLocalDs.getDomain() + HttpConstUrl.URL_CONFIG_FRONT),
                {
                    it?.let {
                        val imgHost = it.imgurl
                        if (!imgHost.isSpace()) {
                            imgLocalDs.saveImgDomain(imgHost)
                            paramsSuccess(imgHost)
                        }
                    }
                }
            )
        }
    }

}