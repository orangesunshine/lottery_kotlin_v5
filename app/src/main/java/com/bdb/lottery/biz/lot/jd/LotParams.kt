package com.bdb.lottery.biz.lot.jd

import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer2Item

data class LotParams(
    val playId: Int,//玩法ID
    val isZeroBonus: Boolean,//是否零返点
    val digit: String?,//位置
    val multiple: Int,//倍数
    val betNums: String?,//投注号码
    val selectedNums: List<List<String>>?,//被选择的号码
    val userSelectedRebate: Double = 0.0,//用户所选返点
    val userBonus: Double = 0.0,//用户返点
    val amount: Double = 0.0,//金额
    val singleMoney: Double = 0.0,//单注奖金金额
    val maxBonus: String?,//理论最高奖金
    val noteCount: Int,//注数
    val isSingle: Boolean,//是否单式
    val totalDigit: String?,//总digit
//    val playName: String?,//玩法名称
//    val subPlayName: String?,//子玩法名称
    val totalPlayName: String?,//玩法名称全称：五星-直选复式
    val isShowDigit: Boolean,//是否显示位置
    val isBuildAll: Boolean = false,//是否构建所有
    val digitTitles: String?,
    val amountUnit: AmountUnit//金额单位
) {
    //单式投注参数生成
    fun genLotParamsByPlayConfig(
        noteCount: Int,
        multiple: Int,
        betNums: String,
        amountUnit: AmountUnit,
        playConfig: PlayLayer2Item?,
        subPlayMethod: SubPlayMethod?,
    ): LotParams? {
        return subPlayMethod?.let {
            LotParams(
                playId = it.play_method_id,
                false,
                null,
                multiple = multiple,
                betNums = betNums,
                null,
                0.0,
                0.0,
                0.0,
                0.0,
                "",
                noteCount = noteCount,
                isSingle = it.subPlayMethodDesc.isdanshi,
                totalDigit = it.subPlayMethodDesc.digit,
                totalPlayName = playConfig?.getPlayTitle(),
                isShowDigit = it.subPlayMethodDesc.is_need_show_weizhi,
                isBuildAll = it.subPlayMethodDesc.isBuildAll,
                digitTitles = it.subPlayMethodDesc.digit_titles,
                amountUnit
            )
        }
    }
}

enum class AmountUnit {
    YUAN, JIAO, FEN, LI
}