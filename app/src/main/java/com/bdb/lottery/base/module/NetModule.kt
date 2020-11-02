package com.bdb.lottery.base.module

import com.bdb.lottery.datasource.account.AccountApi
import com.bdb.lottery.datasource.app.AppApi
import com.bdb.lottery.datasource.domain.DomainApi
import com.bdb.lottery.datasource.game.GameApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit

@Module
@InstallIn(ApplicationComponent::class)
class NetModule {

    ///////////////////////////////////////////////////////////////////////////
    // 账户：登录、试玩、是否需要验证码
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    fun accountApi(retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // app前端配置、客服、版本信息
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    fun appApi(retrofit: Retrofit): AppApi {
        return retrofit.create(AppApi::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 域名：读取域名配置
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    fun domainApi(retrofit: Retrofit): DomainApi {
        return retrofit.create(DomainApi::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 游戏：初始化、全部、大类每个、三方平台、其他平台
    ///////////////////////////////////////////////////////////////////////////
    @Provides
    fun gameApi(retrofit: Retrofit): GameApi {
        return retrofit.create(GameApi::class.java)
    }
}