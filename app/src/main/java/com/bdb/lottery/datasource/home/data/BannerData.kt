package com.bdb.lottery.datasource.home.data

data class BannerData(
    var list: List<BannerItemData>,
    var serverUrl: String
)

data class BannerItemData(
    var createtime: Long,
    var id: Int,
    var imgurl: String,
    var name: String,
    var remarks: String,
    var sort: Int,
    var timebegin: Long,
    var timeend: Long,
    var type: Int
)

data class BannerMapper(val imgUrl: String, val needJump: Boolean, val jumpTitle: String?)