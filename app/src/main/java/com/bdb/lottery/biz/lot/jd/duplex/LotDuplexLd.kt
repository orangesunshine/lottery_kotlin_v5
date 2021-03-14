package com.bdb.lottery.biz.lot.jd.duplex

data class LotDuplexLd(
    val gameType: Int,
    val betTypeId: Int,
    val isBuildAll: Boolean?,
    val digitsTitle: String?,
    val ballTextList: List<String>?,
    val lotDuplexDatas: MutableList<LotDuplexData>,
)