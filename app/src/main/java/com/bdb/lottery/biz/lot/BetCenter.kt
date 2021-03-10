package com.bdb.lottery.biz.lot

import com.bdb.lottery.algorithm.*
import com.bdb.lottery.const.GAME

object BetCenter {
    //region 单式
    var newNums: String? = null

    fun computeSingleAvailableBetCount(
        numsStr: String,
        lotteryType: Int,
        betTypeId: Int,
        digit: String?,
        fromInput: Boolean
    ): Int {
        var available = 0
        val newNumsBuilder = StringBuilder()
        val nums = numsStr.split(",".toRegex()).toTypedArray()
        if (nums.isNotEmpty()) {
            for (num in nums) {
                val count: Int = computeStakeCount(num, lotteryType, betTypeId, digit)
                if (count > 0) available += count
                if (count >= 1) {
                    newNumsBuilder.append(num).append(",")
                }
            }
        }
        newNums =
            if (fromInput || newNumsBuilder.isEmpty()) newNumsBuilder.toString() else newNumsBuilder.deleteCharAt(
                newNumsBuilder.length - 1
            ).toString()
        return available
    }

    /**
     * 计算注数
     *
     * @param betNumStr 传入的数组 ,最大五位
     * 有三种格式     直选：123456|234567|365478|123456    单式与混合：123,234,478,236,789   组合:123456
     * 单选
     * @return
     * @throws
     */
    fun computeStakeCount(
        betNumStr: String?,
        lotteryType: Int,
        subPlayId: Int,
        digit: String?
    ): Int {
        var count = 0
        when (lotteryType) {
            GAME.TYPE_GAME_SSC -> count = CCSCalculate.stake(betNumStr, subPlayId, digit)
            GAME.TYPE_GAME_11X5 -> count =
                ElevenChoiceFiveCalculate.stake(betNumStr, subPlayId)
            GAME.TYPE_GAME_FREQUENCY_LOW -> count =
                LowRrequencyCalculate.stake(betNumStr, subPlayId)
            GAME.TYPE_GAME_PK8, GAME.TYPE_GAME_PK10 -> count =
                PK10Caculate.stake(betNumStr, subPlayId, null)
            GAME.TYPE_GAME_K3 -> count = K3Caculate.stake(betNumStr, subPlayId)
            GAME.TYPE_GAME_KL10FEN -> count = KL10FCaculate.stake(betNumStr, subPlayId)
            GAME.TYPE_GAME_PC28 -> count = PC28Calculate.stake(betNumStr, subPlayId)
        }
        return count
    }
    //endregion

    //region 复式
    /**
     * 构建号码字符串(如：1,2|4,3,5|4)
     *
     * @param betNums
     * @param isBuildAll 是否需要构建所有“|”
     * @return
     */
    fun makeBetNumStr(
        betNums: List<List<String?>?>?,
        isBuildAll: Boolean?,
        digitTitles: String?
    ): String {
        var digitTitlesArray: Array<String?>? = null
        if (digitTitles != null && digitTitles != "") {
            digitTitlesArray = digitTitles.split(",".toRegex()).toTypedArray()
        }
        val sb = StringBuilder()
        if (betNums != null) {
            for (i in betNums.indices) {
                val nums = betNums[i]
                if (nums == null) {
                    if (isBuildAll == true) sb.append("|")
                    continue
                }
                if (digitTitlesArray != null && digitTitlesArray.size == betNums.size) sb.append(
                    digitTitlesArray[i]
                )
                for (j in nums.indices) {
                    val num = nums[j]
                    sb.append(num)
                    //如果是该组最后一个并且没有达到末尾
                    if (j < nums.size - 1) {
                        sb.append(",")
                    }
                }
                sb.append("|")
            }
        }
        //删除最后一个"|"
        if (sb.length >= 1) {
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }
    //endregion

}