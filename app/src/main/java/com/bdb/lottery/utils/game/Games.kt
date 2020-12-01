package com.bdb.lottery.utils.game

import com.bdb.lottery.const.IGame
import com.bdb.lottery.extension.isSpace

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
            val isPc28 = gameType == IGame.TYPE_GAME_PC28
            return if (issue.length > 8 && !isPc28) issue.substring(4) else issue
        }
    }
}