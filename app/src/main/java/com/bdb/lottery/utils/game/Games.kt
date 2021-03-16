package com.bdb.lottery.utils.game

import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.isSpace
import org.apache.commons.lang3.StringUtils

object Games {
    /**
     * 期号文字过长，根据彩种处理成适当长度期号
     */
    fun shortIssueText(issue: String, gameType: Int): String {
        if (issue.isSpace()) return issue
        if (issue.contains("-")) {
            val split = issue.split("-")
            return split[split.size - 1]
        } else {
            val isPc28 = gameType == GAME.TYPE_GAME_PC28
            return if (issue.length > 8 && !isPc28) issue.substring(4) else issue
        }
    }

    fun isSSC(gameType: Int): Boolean {
        return gameType == GAME.TYPE_GAME_SSC
    }

    fun isK3(gameType: Int): Boolean {
        return gameType == GAME.TYPE_GAME_K3
    }

    fun isPK(gameType: Int): Boolean {
        return gameType == GAME.TYPE_GAME_PK10 || gameType == GAME.TYPE_GAME_PK8
    }

    //region 龙虎和：用于显示奖金
    fun isLHH(singleMode: Boolean, betNums: String?, gameType: Int): Boolean {
        if (singleMode) return false//单式
        return when (gameType) {
            GAME.TYPE_GAME_SSC -> return StringUtils.equalsAny(betNums, "龙", "虎", "和")
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> return StringUtils.equalsAny(
                betNums,
                "大小单双",
                "和值"
            )
            GAME.TYPE_GAME_K3 -> return StringUtils.equals(betNums, "和值")
            else -> false
        }
    }
    //endregion
}