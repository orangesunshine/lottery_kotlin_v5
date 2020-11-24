package com.bdb.lottery.module.application

import com.bdb.lottery.datasource.account.AccountApi
import com.bdb.lottery.datasource.app.AppApi
import com.bdb.lottery.datasource.domain.DomainApi
import com.bdb.lottery.datasource.game.GameApi
import com.bdb.lottery.datasource.home.HomeApi
import com.bdb.lottery.datasource.lot.LotApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetModule {

    ///////////////////////////////////////////////////////////////////////////
    // 账户：登录，试玩，是否需要验证码
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    @Singleton
    fun accountApi(retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // app前端配置，客服，版本信息
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    @Singleton
    fun appApi(retrofit: Retrofit): AppApi {
        return retrofit.create(AppApi::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 域名：读取域名配置
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    @Singleton
    fun domainApi(retrofit: Retrofit): DomainApi {
        return retrofit.create(DomainApi::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 游戏：初始化，全部，大类每个，三方平台，其他平台
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    @Singleton
    fun gameApi(retrofit: Retrofit): GameApi {
        return retrofit.create(GameApi::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 首页home：通知，轮播图
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    @Singleton
    fun homeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // lot:获取当前、未来期
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    @Singleton
    fun lotApi(retrofit: Retrofit): LotApi {
        return retrofit.create(LotApi::class.java)
    }
}