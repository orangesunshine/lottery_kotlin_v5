package com.bdb.lottery.datasource.domain.data

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.app.data.PlatformData
import com.bdb.lottery.datasource.domain.DomainApi
import com.bdb.lottery.extension.isDomainUrl
import io.reactivex.rxjava3.core.Observable

data class DomainConfigData(
    val domains: String,
    val versionInfo: VersionInfo
) {
    data class VersionInfo(
        val androidUpgradeContent: String,
        val androidUpgradeForce: Boolean,
        val androidVersionCode: Int,
        val androidVersionName: String,
        val apkUrl: String,
        val iosUpgradeContent: String,
        val iosUpgradeForce: Boolean,
        val iosVersionCode: Int,
        val iosVersionName: String,
        val ipaUrl: String
    )

    fun mapperDomainObs(domainApi: DomainApi): List<Observable<BaseResponse<PlatformData>>> {
        val split = domains.split("@")
        return split.filter { it.isDomainUrl() }.map {
            domainApi.urlPlatformParams(
                it + URL.URL_PLATFORM_PARAMS
            )
        }
    }
}