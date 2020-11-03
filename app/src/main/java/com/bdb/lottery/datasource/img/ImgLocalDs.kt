package com.bdb.lottery.datasource.img

import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.cache.Cache
import javax.inject.Inject
import javax.inject.Singleton

//内存、缓存配置
@Singleton
class ImgLocalDs @Inject constructor() {
    private var mImgUrl: String? = null


    fun saveImgDomain(imgUrl: String): Boolean {
        val space = imgUrl.isSpace()
        if (!space) Cache.putString(ICache.IMG_URL_CACHE, imgUrl)
        return !space
    }

    fun getImgDomain(): String? {
        return Cache.getString(ICache.IMG_URL_CACHE)
    }
}