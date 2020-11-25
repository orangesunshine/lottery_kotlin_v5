package com.bdb.lottery.utils

import com.bdb.lottery.R
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_KL10FEN
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_K3
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_LHC
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_PC28
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_PK10
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_PK8
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_FREQUENCY_LOW
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_SSC
import com.bdb.lottery.const.IGame.Companion.TYPE_GAME_11X5
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.isSpace
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TGame @Inject constructor() {
    /**
     * 首页home->全部游戏->游戏大类图片
     */
    fun gameTypeDrawable(gameType: Int): Int {
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
     * 期号文字过长，根据彩种处理成适当长度期号
     */
    fun shortIssueText(issue: String, gameType: String): String {
        if (issue.isSpace()) return issue
        if (issue.contains("-")) {
            val split = issue.split("-")
            return split[split.size - 1]
        } else {
            val isPc28 = gameType.equalsNSpace(TYPE_GAME_PC28.toString())
            return if (issue.length > 8 && !isPc28) issue.substring(4) else issue
        }
    }

    /**
     * 彩种对应玩法开奖号码是否显示alpha
     */
    private fun lotShowAlpha(gameType: String): Boolean {
        return arrayOf(
            TYPE_GAME_PK10.toString(),
            TYPE_GAME_PK8.toString(),
            TYPE_GAME_SSC.toString(),
            TYPE_GAME_11X5.toString(),
            TYPE_GAME_FREQUENCY_LOW.toString()
        ).contains(gameType)
    }

    /**
     * 彩种对应玩法非alpha列表
     */
    fun lotAlphaList(playTypeName: String, gameType: String): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        if (playTypeName.isSpace() || !lotShowAlpha(gameType)) {
            return list
        }
        val stringBuffer = StringBuffer()
        var max = 0
        if (gameType.equalsNSpace(TYPE_GAME_PK10.toString())) max = 10
        else if (gameType.equalsNSpace(TYPE_GAME_PK8.toString())) max = 8
        else if (gameType.equalsNSpace(TYPE_GAME_SSC.toString())
            || gameType.equalsNSpace(TYPE_GAME_11X5.toString())
        ) max = 5
        else if (gameType.equalsNSpace(TYPE_GAME_FREQUENCY_LOW.toString())) max = 3

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
                stringBuffer.append(i)
            }
        } else if (playTypeName.contains("前")) {
            for (i in 1..index) {
                stringBuffer.append(i)
            }
        } else if (playTypeName.contains("第")) {
            stringBuffer.append(index)
        } else if (playTypeName.contains("1V10")) {
            stringBuffer.append(1).append(10)
        } else if (playTypeName.contains("2V9")) {
            stringBuffer.append(2).append(9)
        } else if (playTypeName.contains("3V8")) {
            stringBuffer.append(3).append(8)
        } else if (playTypeName.contains("4V7")) {
            stringBuffer.append(4).append(7)
        } else if (playTypeName.contains("5V6")) {
            stringBuffer.append(5).append(6)
        } else if (playTypeName.contains("1V8")) {
            stringBuffer.append(1).append(8)
        } else if (playTypeName.contains("2V7")) {
            stringBuffer.append(2).append(7)
        } else if (playTypeName.contains("3V6")) {
            stringBuffer.append(3).append(6)
        } else if (playTypeName.contains("4V5")) {
            stringBuffer.append(4).append(5)
        } else if (playTypeName.contains("冠亚")) {
            stringBuffer.append(1).append(2)
        }

        if (stringBuffer.toString().isSpace()) {
            when (gameType) {
                TYPE_GAME_SSC.toString(), TYPE_GAME_11X5.toString() ->
                    stringBuffer.append(1).append(2).append(3).append(4).append(5)
                TYPE_GAME_FREQUENCY_LOW.toString() -> stringBuffer.append(1).append(2).append(3)
                TYPE_GAME_PK10.toString() -> stringBuffer.append(1).append(2).append(3).append(4)
                    .append(5).append(6).append(7).append(8).append(9).append(10)
                TYPE_GAME_PK8.toString() -> stringBuffer.append(1).append(2).append(3).append(4)
                    .append(5).append(6).append(7).append(8)
            }
        }
        return list
    }
}