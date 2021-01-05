package com.bdb.lottery.utils.game

import com.bdb.lottery.R
import com.bdb.lottery.const.GAME
import com.bdb.lottery.datasource.cocos.TCocos
import com.bdb.lottery.extension.isSpace
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class TGame @Inject constructor(private val tCocos: TCocos) {
    /**
     * 首页home->全部游戏->游戏大类图片
     */
    fun gameTypeRoundDr(gameType: Int): Int {
        var drawable: Int = R.drawable.home_placeholder_round_img_ic
        when (gameType) {
            GAME.TYPE_GAME_SSC -> drawable =
                R.drawable.home_allgame_type_ssc
            GAME.TYPE_GAME_11X5 -> drawable =
                R.drawable.home_allgame_type_11x5
            GAME.TYPE_GAME_PK10 -> drawable =
                R.drawable.home_allgame_type_pk10
            GAME.TYPE_GAME_FREQUENCY_LOW -> drawable =
                R.drawable.home_allgame_type_lows
            GAME.TYPE_GAME_K3 -> drawable =
                R.drawable.home_allgame_type_k3
            GAME.TYPE_GAME_KL10FEN -> drawable =
                R.drawable.home_allgame_type_klc
            GAME.TYPE_GAME_LHC -> drawable =
                R.drawable.home_allgame_type_lhc
            GAME.TYPE_GAME_PC28 -> drawable =
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
            GAME.TYPE_GAME_PK10,
            GAME.TYPE_GAME_PK8,
            GAME.TYPE_GAME_SSC,
            GAME.TYPE_GAME_11X5,
            GAME.TYPE_GAME_FREQUENCY_LOW
        ).contains(gameType)
    }

    /**
     * 彩种对应玩法非alpha列表
     */
    fun brightIndexs(playTypeName: String?, gameType: Int): List<Int> {
        val list = ArrayList<Int>()
        if (!playTypeName.isSpace() && canShowAlpha(gameType)) {
            var max = 0
            if (gameType == GAME.TYPE_GAME_PK10) max = 10
            else if (gameType == GAME.TYPE_GAME_PK8) max = 8
            else if (gameType == GAME.TYPE_GAME_SSC
                || gameType == GAME.TYPE_GAME_11X5
            ) max = 5
            else if (
                gameType == GAME.TYPE_GAME_FREQUENCY_LOW) max = 3

            var index = 1
            if (playTypeName!!.contains("一")) {
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
        }

        if (list.isEmpty()) {
            when (gameType) {
                GAME.TYPE_GAME_SSC, GAME.TYPE_GAME_11X5 ->
                    list.addAll(listOf(1, 2, 3, 4, 5))
                GAME.TYPE_GAME_FREQUENCY_LOW -> list.addAll(listOf(1, 2, 3))
                GAME.TYPE_GAME_PK10 -> list.addAll(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                GAME.TYPE_GAME_PK8 -> list.addAll(listOf(1, 2, 3, 4, 5, 6, 7, 8))
            }
        }
        return list
    }

    fun getShowLotteryPlace(playName: String?, gameType: String): String {
        return when (gameType) {
            "1", "2", "3", "9" -> getShowLotteryPlaceSSC(playName, gameType)
            "5", "11" -> getShowLotteryPlacePK10(playName, gameType)
            else -> ""
        }
    }

    private fun getShowLotteryPlaceSSC(playName: String?, gameType: String): String {
        if (playName.isSpace()) {
            return ""
        }
        val stringBuffer = StringBuffer()
        if (playName!!.contains("五星")) {
            stringBuffer.append(1).append(2).append(3).append(4).append(5)
        } else if (playName.contains("四星")) {
            stringBuffer.append(2).append(3).append(4).append(5)
        } else if (playName.contains("后三")) {
            stringBuffer.append(3).append(4).append(5)
        } else if (playName.contains("前三")) {
            stringBuffer.append(1).append(2).append(3)
        } else if (playName.contains("中三")) {
            stringBuffer.append(2).append(3).append(4)
        } else if (playName.contains("后二")) {
            stringBuffer.append(4).append(5)
        } else if (playName.contains("前二")) {
            stringBuffer.append(1).append(2)
        } else if (playName.contains("第一位")) {
            stringBuffer.append(1)
        } else if (playName.contains("第二位")) {
            stringBuffer.append(2)
        } else if (playName.contains("第三位")) {
            stringBuffer.append(3)
        } else if (playName.contains("第四位")) {
            stringBuffer.append(4)
        } else if (playName.contains("第五位")) {
            stringBuffer.append(5)
        } else if (playName.contains("第六位")) {
            stringBuffer.append(6)
        } else if (playName.contains("第七位")) {
            stringBuffer.append(7)
        } else if (playName.contains("第八位")) {
            stringBuffer.append(8)
        } else {
            val list1 = Arrays.asList(*playName.split("".toRegex()).toTypedArray())
            if (list1.contains("万")) {
                stringBuffer.append(1)
            }
            if (list1.contains("千")) {
                stringBuffer.append(2)
            }
            if (list1.contains("百")) {
                stringBuffer.append(3)
            }
            if (list1.contains("十")) {
                stringBuffer.append(4)
            }
            if (list1.contains("个")) {
                stringBuffer.append(5)
            }
        }
        if (gameType == "2" && playName.contains("三星")) {
            stringBuffer.append(1).append(2).append(3)
        }
        if (gameType == "2" && playName.contains("后二")) {
            stringBuffer.append(2).append(3)
        }
        if (gameType == "9" && playName.contains("后三")) {
            stringBuffer.append(7).append(8)
        }
        if (stringBuffer.toString().isSpace()) {
            when (gameType) {
                "1", "3" -> stringBuffer.append(1).append(2).append(3).append(4).append(5)
                "2" -> stringBuffer.append(1).append(2).append(3)
            }
        }

        return stringBuffer.toString()
    }

    private fun getShowLotteryPlacePK10(playName: String?, gameType: String?): String {
        val list: MutableList<String> = java.util.ArrayList()
        list.clear()
        if (playName.isSpace()) {
            return ""
        }
        val stringBuffer = StringBuffer()
        if (playName!!.contains("前一")) {
            stringBuffer.append(1)
        } else if (playName.contains("前二") || playName.contains("冠亚")) {
            stringBuffer.append(1).append(2)
        } else if (playName.contains("前三")) {
            stringBuffer.append(1).append(2).append(3)
        } else if (playName.contains("前四")) {
            stringBuffer.append(1).append(2).append(3).append(4)
        } else if (playName.contains("前五")) {
            stringBuffer.append(1).append(2).append(3).append(4).append(5)
        } else if (playName.contains("1V10")) {
            stringBuffer.append(1).append("*")
        } else if (playName.contains("2V9")) {
            stringBuffer.append(2).append(9)
        } else if (playName.contains("3V8")) {
            stringBuffer.append(3).append(8)
        } else if (playName.contains("4V7")) {
            stringBuffer.append(4).append(7)
        } else if (playName.contains("5V6")) {
            stringBuffer.append(5).append(6)
        } else if (playName.contains("1V8")) {
            stringBuffer.append(1).append(8)
        } else if (playName.contains("2V7")) {
            stringBuffer.append(2).append(7)
        } else if (playName.contains("3V6")) {
            stringBuffer.append(3).append(6)
        } else if (playName.contains("4V5")) {
            stringBuffer.append(4).append(5)
        }
        if (stringBuffer.toString().isSpace()) {
            when (gameType) {
                "5" -> stringBuffer.append(1).append(2).append(3).append(4).append(5).append(6)
                    .append(7).append(8).append(9).append("*")
                "11" -> stringBuffer.append(1).append(2).append(3).append(4).append(5).append(6)
                    .append(7).append(8)
            }
        }
        return stringBuffer.toString()
    }

    //投注页面cocos动画、直播入口（奖杯）是否显示
    fun cupVisible(gameType: Int, gameId: Int): Boolean {
        return tCocos.hasCocosAnim(gameType, gameId) || hasLive(gameType, gameId)
    }

    //直播存在
    private fun hasLive(gameType: Int, gameId: Int): Boolean {
        return false
    }
}