package com.bdb.lottery.datasource.lot.data

data class LotParam(
    var gameId: String,
    var gameName: String,
    var issueNo: String,
    var kg: Boolean,
    var tingZhiZhuiHao: Boolean,
    var token: String,
    var touZhuHaoMa: List<TouZhuHaoMa>?,
    var zhuiHaoQiHao: List<ZhuiHaoQiHao>?
)

data class TouZhuHaoMa(
    var ITEM_TYPE: Int,
    var amount: Double,
    var baseScale: Double,
    var bouse: Double,
    var danZhuJinEDanWei: Int,
    var digit: String,
    var isDanTiao: Boolean,
    var model: String,
    var playtypename: String,
    var singleBet: SingleBet,
    var singleMoney: Double,
    var touZhuBeiShu: Int,
    var touZhuHaoMa: String,
    var wanFaID: Int,
    var yongHuSuoTiaoFanDian: Double,
    var zhuShu: Int
)

data class ZhuiHaoQiHao(
    var beiShu: Int,
    var qiHao: String
)

data class SingleBet(
    var isSingleBet: Boolean,
    var maxMoney: Double
)