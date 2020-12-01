package com.bdb.lottery.utils.game

import com.bdb.lottery.R
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_11X5
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_FREQUENCY_LOW
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_K3
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_KL10FEN
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_LHC
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_PC28
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_PK10
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_PK8
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_SSC
import com.bdb.lottery.extension.isSpace
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TGame @Inject constructor() {
    /**
     * 首页home->全部游戏->游戏大类图片
     */
    fun gameTypeRoundDr(gameType: Int): Int {
        var drawable: Int = R.drawable.home_placeholder_round_img_ic
        when (gameType) {
            TYPE_GAME_SSC -> drawable =
                R.drawable.home_allgame_type_ssc
            TYPE_GAME_11X5 -> drawable =
                R.drawable.home_allgame_type_11x5
            TYPE_GAME_PK10 -> drawable =
                R.drawable.home_allgame_type_pk10
            TYPE_GAME_FREQUENCY_LOW -> drawable =
                R.drawable.home_allgame_type_lows
            TYPE_GAME_K3 -> drawable =
                R.drawable.home_allgame_type_k3
            TYPE_GAME_KL10FEN -> drawable =
                R.drawable.home_allgame_type_klc
            TYPE_GAME_LHC -> drawable =
                R.drawable.home_allgame_type_lhc
            TYPE_GAME_PC28 -> drawable =
                R.drawable.home_allgame_type_pc28
        }
        return drawable
    }

    /**
     * 期号文字过长，根据彩种处理成适当长度期号，拼接gameName
     */
    fun shortIssueTextWithGameName(issue: String, gameName: String?, gameType: Int): String {
        return StringBuilder().append(gameName ?: "").append(" ")
            .append(Games.shortIssueText(issue, gameType)).append("期")
            .toString()
    }

    /**
     * 彩种对应玩法开奖号码是否显示alpha
     */
    private fun canShowAlpha(gameType: Int): Boolean {
        return arrayOf(
            TYPE_GAME_PK10,
            TYPE_GAME_PK8,
            TYPE_GAME_SSC,
            TYPE_GAME_11X5,
            TYPE_GAME_FREQUENCY_LOW
        ).contains(gameType)
    }

    /**
     * 彩种对应玩法非alpha列表
     */
    fun brightIndexs(playTypeName: String, gameType: Int): List<Int> {
        if (playTypeName.isSpace() || !canShowAlpha(gameType)) {
            return emptyList()
        }
        val list = ArrayList<Int>()
        var max = 0
        if (gameType == TYPE_GAME_PK10) max = 10
        else if (gameType == TYPE_GAME_PK8) max = 8
        else if (gameType == TYPE_GAME_SSC
            || gameType == TYPE_GAME_11X5
        ) max = 5
        else if (
            gameType == TYPE_GAME_FREQUENCY_LOW) max = 3

        var index = 1
        if (playTypeName.contains("一")) {
            index = 1
        } else if (playTypeName.contains("二")) {
            index = 2
        } else if (playTypeName.contains("三")) {
            index = 3
        } else if (playTypeName.contains("四")) {
            index = 4
        } else if (playTypeName.contains("五")) {
            index = 5
        } else if (playTypeName.contains("六")) {
            index = 6
        } else if (playTypeName.contains("七")) {
            index = 7
        } else if (playTypeName.contains("八")) {
            index = 8
        }

        if (playTypeName.contains("后") || playTypeName.contains("星")) {
            var start = max - index + 1
            if (start < 1) start = 1
            for (i in start..max) {
                list.add(i)
            }
        } else if (playTypeName.contains("前")) {
            for (i in 1..index) {
                list.add(i)
            }
        } else if (playTypeName.contains("第")) {
            list.add(index)
        } else if (playTypeName.contains("1V10")) {
            list.add(1)
            list.add(10)
        } else if (playTypeName.contains("2V9")) {
            list.add(2)
            list.add(9)
        } else if (playTypeName.contains("3V8")) {
            list.add(3)
            list.add(8)
        } else if (playTypeName.contains("4V7")) {
            list.add(4)
            list.add(7)
        } else if (playTypeName.contains("5V6")) {
            list.add(5)
            list.add(6)
        } else if (playTypeName.contains("1V8")) {
            list.add(1)
            list.add(8)
        } else if (playTypeName.contains("2V7")) {
            list.add(2)
            list.add(7)
        } else if (playTypeName.contains("3V6")) {
            list.add(3)
            list.add(6)
        } else if (playTypeName.contains("4V5")) {
            list.add(4)
            list.add(5)
        } else if (playTypeName.contains("冠亚")) {
            list.add(1)
            list.add(2)
        }

        if (list.isEmpty()) {
            when (gameType) {
                TYPE_GAME_SSC, TYPE_GAME_11X5 ->
                    list.addAll(listOf(1, 2, 3, 4, 5))
                TYPE_GAME_FREQUENCY_LOW -> list.addAll(listOf(1, 2, 3))
                TYPE_GAME_PK10 -> list.addAll(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                TYPE_GAME_PK8 -> list.addAll(listOf(1, 2, 3, 4, 5, 6, 7, 8))
            }
        }
        return list
    }
}