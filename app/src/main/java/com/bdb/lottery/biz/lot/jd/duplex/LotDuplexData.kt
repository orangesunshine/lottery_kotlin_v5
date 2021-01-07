package com.bdb.lottery.biz.lot.jd.duplex

data class LotDuplexData(
    val label: String?,//万千百十个
    val itemBallNumCounts: Int,//每组号码球个数
    val ballTextList: List<String>?, //不为空，则说明球显示的不是数字号码，比如龙虎玩法显示：龙、虎、和
    val dxdsVisible: Boolean,//是否显示大小单双
    val hotVisible: Boolean,//冷热显示
    val leaveVisible: Boolean,//遗漏显示
    val zeroVisible: Boolean,//一位数是否添加0开头(11选5、pk10)
) {
    fun genBallDatas(): MutableList<String> {
        return ballTextList?.toMutableList() ?: let {
            val list = mutableListOf<String>()
            for (i in 0 until itemBallNumCounts) {
                list.add(if (i < 10 && zeroVisible) "0$i" else i.toString())
            }
            list
        }
    }
}