package com.bdb.lottery.biz.lot.jd.duplex

data class LotDuplexData(
    val label: String,//万千百十个
    val groupCount: Int,//复式组数
    val dxdsVisible: Boolean,//是否显示大小单双
    val hotVisible: Boolean,//冷热显示
    val leaveVisible: Boolean,//遗漏显示
    val zeroVisible: Boolean//一位数是否添加0开头(11选5、pk10)
) {
}