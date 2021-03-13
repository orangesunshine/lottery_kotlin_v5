package com.bdb.lottery.biz.main.home

import androidx.fragment.app.Fragment
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.datasource.home.data.BannerMapper
import com.bdb.lottery.extension.start
import com.youth.banner.listener.OnBannerListener

class HomeBannerClickListener(private val fragment: Fragment) : OnBannerListener<BannerMapper> {
    override fun OnBannerClick(data: BannerMapper?, position: Int) {
        if (data?.needJump == true) fragment.start<LoginActivity>()
    }
}