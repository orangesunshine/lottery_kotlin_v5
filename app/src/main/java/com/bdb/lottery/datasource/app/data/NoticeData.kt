package com.bdb.lottery.datasource.app.data

data class NoticeData(
    var roll: List<Roll>
)

data class Roll(
    var content: String,
    var createtime: String,
    var sort: Int,
    var timebegin: String,
    var timeend: String,
    var title: String,
    var type: String
)