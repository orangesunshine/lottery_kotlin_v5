package com.bdb.lottery.datasource.game.data

data class ThirdPlatformDataItem(
    var bonus: String,
    var isOpen: Boolean,
    var thirdCode: String,
    var thirdName: String,
    var thirdPlatId: Int
)