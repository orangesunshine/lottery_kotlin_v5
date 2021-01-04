package com.bdb.lottery.utils.lot

import android.text.TextUtils
import com.bdb.lottery.R
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.validIndex
import kotlin.math.max
import kotlin.math.min

object Lots {
    //region 形态
    fun getLabel(gameType: Int, parentPlayId: Int, playId: Int, nums: String?): String {
        if (nums.isSpace()) {
            return ""
        }
        val numList = ArrayList<String>()
        val ss = nums!!.split(" ".toRegex())
        if (GAME.TYPE_GAME_SSC == gameType) {
            if (parentPlayId == 21) {
                return get5StarType(nums)
            } else if (parentPlayId == 22) {
                numList.add(ss[1])
                numList.add(ss[2])
                numList.add(ss[3])
                numList.add(ss[4])
                return get4StarType(numList)
            } else if (parentPlayId == 23) {
                numList.add(ss[2])
                numList.add(ss[3])
                numList.add(ss[4])
                if (playId == 203) {
                    return get3StartKuaType(numList)
                }
                return if (playId == 423) {
                    get3StartHeType(numList)
                } else get3StarType(numList)
            } else if (parentPlayId == 24) {
                numList.add(ss[1])
                numList.add(ss[2])
                numList.add(ss[3])
                if (playId == 205) {
                    return get3StartKuaType(numList)
                }
                return if (playId == 425) {
                    get3StartHeType(numList)
                } else get3StarType(numList)
            } else if (parentPlayId == 25) {
                numList.add(ss[0])
                numList.add(ss[1])
                numList.add(ss[2])
                if (playId == 204) {
                    return get3StartKuaType(numList)
                }
                return if (playId == 424) {
                    get3StartHeType(numList)
                } else get3StarType(numList)
            } else if (parentPlayId == 27 || parentPlayId == 138 || parentPlayId == 139) {
                numList.add(ss[0])
                numList.add(ss[1])
                if (playId == 207) {
                    return get2StarKuaType(numList)
                }
                return if (playId == 432) {
                    get2StarType(numList)
                } else get2StarType(numList)
            } else if (parentPlayId == 26) {
                numList.add(ss[3])
                numList.add(ss[4])
                if (playId == 206) {
                    return get2StarKuaType(numList)
                }
                return if (playId == 431) {
                    get2StarType(numList)
                } else get2StarType(numList)
            } else if (parentPlayId == 28 || parentPlayId == 29 || parentPlayId == 45 || parentPlayId == 46 || parentPlayId == 47) {
                return ""
            } else if (parentPlayId == 223) {
                if (playId == 3189) {
                    //万千
                    numList.add(ss[0])
                    numList.add(ss[1])
                } else if (playId == 3190) {
                    numList.add(ss[0])
                    numList.add(ss[2])
                    //万百
                } else if (playId == 3191) {
                    //万十
                    numList.add(ss[0])
                    numList.add(ss[3])
                } else if (playId == 3192) {
                    //万个
                    numList.add(ss[0])
                    numList.add(ss[4])
                } else if (playId == 3193) {
                    numList.add(ss[1])
                    numList.add(ss[2])
                } else if (playId == 3194) {
                    //千十
                    numList.add(ss[1])
                    numList.add(ss[3])
                } else if (playId == 3195) {
                    //千个
                    numList.add(ss[1])
                    numList.add(ss[4])
                } else if (playId == 3196) {
                    //百十
                    numList.add(ss[2])
                    numList.add(ss[3])
                } else if (playId == 3197) {
                    //百个
                    numList.add(ss[2])
                    numList.add(ss[4])
                } else if (playId == 3198) {
                    numList.add(ss[3])
                    numList.add(ss[4])
                }
                return getLonghuType(numList)
            } else if (parentPlayId == 224) {
                if (playId == 3200) {
                    //前二
                    numList.add(ss[0])
                    numList.add(ss[1])
                } else if (playId == 3201) {
                    numList.add(ss[3])
                    numList.add(ss[4])
                    //后二
                }
                return getDaXiaoDanType(numList)
            }
        } else if (GAME.TYPE_GAME_PC28 == gameType) {
            return ""
        } else if (GAME.TYPE_GAME_FREQUENCY_LOW == gameType) {
            if (parentPlayId == 168 || parentPlayId == 169) {
                numList.add(ss[0])
                numList.add(ss[1])
                numList.add(ss[2])
                return get3StarType(
                    numList
                )
            }
        } else if (GAME.TYPE_GAME_K3 == gameType) {
            numList.add(ss[0])
            numList.add(ss[1])
            numList.add(ss[2])
            if (parentPlayId == 247) {
                return get3StartHeType(numList)
            } else if (parentPlayId == 252) {
                return get3StartLianType(numList)
            }
            return get3StartKuaiType(
                numList
            )
        } else if (GAME.TYPE_GAME_PK10 == gameType) {
            if (parentPlayId == 245) {
                if (playId == 3206) {
                    //1V10
                    numList.add(ss[0])
                    numList.add(ss[9])
                } else if (playId == 3207) {
                    //2V9
                    numList.add(ss[1])
                    numList.add(ss[8])
                } else if (playId == 3208) {
                    //3V8
                    numList.add(ss[2])
                    numList.add(ss[7])
                } else if (playId == 3209) {
                    //4V7
                    numList.add(ss[3])
                    numList.add(ss[6])
                } else if (playId == 3210) {
                    //5V6
                    numList.add(ss[4])
                    numList.add(ss[5])
                }
                return getLonghuType(
                    numList
                )
            } else if (parentPlayId == 246) {
                numList.add(ss[0])
                numList.add(ss[1])
                return get2StarType(
                    numList
                )
            } else if (parentPlayId == 282) {
                if (playId == 3317) {
                    //1V8
                    numList.add(ss[0])
                    numList.add(ss[7])
                } else if (playId == 3318) {
                    //2V7
                    numList.add(ss[1])
                    numList.add(ss[6])
                } else if (playId == 3319) {
                    //3V6
                    numList.add(ss[2])
                    numList.add(ss[5])
                } else if (playId == 3320) {
                    //4V5
                    numList.add(ss[3])
                    numList.add(ss[4])
                }
                return getLonghuType(
                    numList
                )
            }
        }
        return ""
    }

    //region 五星
    private fun get5StarType(nums: String): String {
        val ss = nums.split(" ".toRegex())
        var i = 0
        if (TextUtils.equals(ss[0], ss[1])) {
            i++
        }
        if (TextUtils.equals(ss[0], ss[2])) {
            i++
        }
        if (TextUtils.equals(ss[0], ss[3])) {
            i++
        }
        if (TextUtils.equals(ss[0], ss[4])) {
            i++
        }
        if (TextUtils.equals(ss[1], ss[2])) {
            i++
        }
        if (TextUtils.equals(ss[1], ss[3])) {
            i++
        }
        if (TextUtils.equals(ss[1], ss[4])) {
            i++
        }
        if (TextUtils.equals(ss[2], ss[3])) {
            i++
        }
        if (TextUtils.equals(ss[2], ss[4])) {
            i++
        }
        if (TextUtils.equals(ss[3], ss[4])) {
            i++
        }
        return get5StarText(i)
    }

    private fun get5StarText(i: Int): String {
        return if (i == 0) {
            "组120"
        } else if (i == 1) {
            "组60"
        } else if (i == 3) {
            "组20"
        } else if (i == 2) {
            "组30"
        } else if (i == 4) {
            "组10"
        } else if (i == 6) {
            "组5"
        } else {
            "--"
        }
    }
    //endregion

    //region 四星
    private fun get4StarType(ss: List<String>): String {
        var i = 0
        if (TextUtils.equals(ss[0], ss[1])) {
            i++
        }
        if (TextUtils.equals(ss[0], ss[2])) {
            i++
        }
        if (TextUtils.equals(ss[0], ss[3])) {
            i++
        }
        if (TextUtils.equals(ss[1], ss[2])) {
            i++
        }
        if (TextUtils.equals(ss[1], ss[3])) {
            i++
        }
        if (TextUtils.equals(ss[2], ss[3])) {
            i++
        }
        return get4StarText(i)
    }

    private fun get4StarText(i: Int): String {
        return if (i == 0) {
            "组24"
        } else if (i == 1) {
            "组12"
        } else if (i == 2) {
            "组6"
        } else if (i == 3) {
            "组4"
        } else {
            "--"
        }
    }
    //endregion

    //region 三星
    private fun get3StartLianType(ss: List<String>): String {
        val num1 = ss[0].toInt()
        val num2 = ss[1].toInt()
        val num3 = ss[2].toInt()
        var max = max(num1, num2)
        max = max(max, num3)
        var min = min(num1, num2)
        min = min(min, num3)
        if (num1 != num2 && num2 != num3) {
            if (max - min == 2) {
                return "三连号"
            }
        }
        return get3StartKuaiType(ss)
    }

    private fun get3StartKuaType(ss: List<String>): String {
        val num1 = ss[0].toInt()
        val num2 = ss[1].toInt()
        val num3 = ss[2].toInt()
        var max = max(num1, num2)
        max = max(max, num3)
        var min = min(num1, num2)
        min = min(min, num3)
        return "跨" + (max - min)
    }


    private fun get3StartKuaiType(ss: List<String>): String {
        var i = 0
        val isSame1 = TextUtils.equals(ss[0], ss[1])
        val isSame2 = TextUtils.equals(ss[0], ss[2])
        val isSame3 = TextUtils.equals(ss[1], ss[2])
        if (isSame1) {
            i++
        }
        if (isSame2) {
            i++
        }
        if (isSame3) {
            i++
        }
        return get3StartK3Text(i)
    }

    private fun get3StarType(ss: List<String>): String {
        var i = 0
        val isSame1 = TextUtils.equals(ss[0], ss[1])
        val isSame2 = TextUtils.equals(ss[0], ss[2])
        val isSame3 = TextUtils.equals(ss[1], ss[2])
        if (isSame1) {
            i++
        }
        if (isSame2) {
            i++
        }
        if (isSame3) {
            i++
        }
        return get3StartText(i)
    }


    private fun get3StartText(i: Int): String {
        return if (i == 0) {
            "组6"
        } else if (i == 1) {
            "组3"
        } else {
            "--"
        }
    }

    private fun get3StartK3Text(i: Int): String {
        return if (i == 0) {
            "三不同"
        } else if (i == 1) {
            "二同号"
        } else if (i == 3) {
            "三同号"
        } else {
            "--"
        }
    }

    private fun get3StartHeType(ss: List<String>): String {
        val i = 0
        val isSame1 = ss[0].toInt() + ss[1].toInt() + ss[2].toInt()
        return isSame1.toString() + ""
    }
    //endregion

    //region 二星
    private fun get2StarKuaType(ss: List<String>): String {
        val num1 = ss[0].toInt()
        val num2 = ss[1].toInt()
        val max = Math.max(num1, num2)
        val min = Math.min(num1, num2)
        return "跨" + (max - min)
    }

    private fun get2StarType(ss: List<String>): String {
        val isSame1 = ss[0].toInt() + ss[1].toInt()
        return isSame1.toString() + ""
    }
    //endregion

    //region 大小单双
    private fun getDaXiaoDanType(ss: List<String>?): String {
        if (ss == null || ss.isEmpty()) return ""
        val num1 = ss[0].toInt()
        val num2 = ss[1].toInt()
        var num1Text = ""
        var num2Text = ""
        num1Text = if (num1 < 5) {
            "小"
        } else {
            "大"
        }
        num2Text = if (num2 < 5) {
            "小"
        } else {
            "大"
        }
        num1Text = if (num1 % 2 == 0) {
            num1Text + "双"
        } else {
            num1Text + "单"
        }
        num2Text = if (num2 % 2 == 0) {
            num2Text + "双"
        } else {
            num2Text + "单"
        }
        return "$num1Text $num2Text"
    }
    //endregion

    //region 龙虎
    private fun getLonghuType(ss: List<String>?): String {
        if (ss == null || ss.isEmpty()) return ""
        val num1 = ss[0].toInt()
        val num2 = ss[1].toInt()
        if (num1 < num2) {
            return "虎"
        } else if (num1 == num2) {
            return "和"
        }
        return "龙"
    }
    //endregion
    //endregion

    //region pk10根据开机号码转换对应图片
    private val ball_pk = arrayOf(R.drawable.lot_history_ball_pk_num1,
        R.drawable.lot_history_ball_pk_num2,
        R.drawable.lot_history_ball_pk_num3,
        R.drawable.lot_history_ball_pk_num4,
        R.drawable.lot_history_ball_pk_num5,
        R.drawable.lot_history_ball_pk_num6,
        R.drawable.lot_history_ball_pk_num7,
        R.drawable.lot_history_ball_pk_num8,
        R.drawable.lot_history_ball_pk_num9,
        R.drawable.lot_history_ball_pk_num10)

    fun num2Ball4Pk(ball: String?): Int {
        ball?.let {
            val index = it.toInt()
            if (ball_pk.validIndex(index)) return@num2Ball4Pk ball_pk[index]
        }
        return -1
    }
    //endregion

    //region PC28
    fun num2Dr4Pc28(ball: String?): Int {
        var numType: Int = ball2ColorType4Pc28(ball)
        if (GAME.IS_PC28) numType = getGame6ColourType(ball)
        return when (numType) {
            GAME_TYPE_COLOR_ORANGE -> R.drawable.lot_history_ball_pc28_orange
            GAME_TYPE_COLOR_GREEN -> R.drawable.lot_history_ball_pc28_green
            GAME_TYPE_COLOR_BLUE -> R.drawable.lot_history_ball_pc28_blue
            GAME_TYPE_COLOR_RED -> R.drawable.lot_history_ball_pc28_red
            else -> R.drawable.lot_history_ball_pc28_red
        }
    }

    /**
     * 号码球颜色选择器
     *
     * @param bet_ball
     * @return
     */
    const val GAME_TYPE_COLOR_ORANGE: Int = 1
    const val GAME_TYPE_COLOR_GREEN = 2
    const val GAME_TYPE_COLOR_BLUE = 3
    const val GAME_TYPE_COLOR_RED = 4
    private fun ball2ColorType4Pc28(ball: String?): Int {
        return when (ball) {
            "0", "5", "6", "11", "16", "17", "21", "22", "27" -> GAME_TYPE_COLOR_GREEN
            "3", "4", "9", "10", "14", "15", "20", "25", "26" -> GAME_TYPE_COLOR_BLUE
            "1", "2", "7", "8", "12", "13", "18", "19", "23", "24" -> GAME_TYPE_COLOR_RED
            else -> GAME_TYPE_COLOR_ORANGE
        }
    }

    /**
     * 号码球颜色选择器
     *
     * @param ball
     * @return
     */
    private fun getGame6ColourType(ball: String?): Int {
        return when (ball) {
            "1", "4", "7", "10", "16", "19", "22", "25" -> GAME_TYPE_COLOR_GREEN
            "2", "5", "8", "11", "17", "20", "23", "26" -> GAME_TYPE_COLOR_BLUE
            "3", "6", "9", "12", "15", "18", "21", "24" -> GAME_TYPE_COLOR_RED
            else -> GAME_TYPE_COLOR_ORANGE//"0", "13", "14", "27"
        }
    }
    //endregion

    //region 快三
    fun ball2Dr4K3(ball: String?): Int {
        ball?.let {
            return when (it.toInt()) {
                1 -> R.drawable.lot_history_ball_k3_1
                2 -> R.drawable.lot_history_ball_k3_2
                3 -> R.drawable.lot_history_ball_k3_3
                4 -> R.drawable.lot_history_ball_k3_4
                5 -> R.drawable.lot_history_ball_k3_5
                6 -> R.drawable.lot_history_ball_k3_6
                else -> 0
            }
        }
        return 0
    }

    fun ball2Dr4K3New(ball: String?): Int {
        ball?.let {
            return when (it.toInt()) {
                1 -> R.drawable.lot_history_ball_k3_new_1
                2 -> R.drawable.lot_history_ball_k3_new_2
                3 -> R.drawable.lot_history_ball_k3_new_3
                4 -> R.drawable.lot_history_ball_k3_new_4
                5 -> R.drawable.lot_history_ball_k3_new_5
                6 -> R.drawable.lot_history_ball_k3_new_6
                else -> 0
            }
        }
        return 0
    }
    //endregion

    //region 六合彩
    fun ball2Dr4LHC(num: String?, isLHC: Boolean): Int {
        return when (if (isLHC) ball2ColorType4LHC(num) else ball2ColorType4LHCByDivide(num)) {
            GAME_TYPE_COLOR_RED -> R.drawable.lot_history_ball_lhc_red
            GAME_TYPE_COLOR_BLUE -> R.drawable.lot_history_ball_lhc_blue
            else -> R.drawable.lot_history_ball_lhc_green//GAME_TYPE_COLOR_GREEN
        }
    }

    /**
     * 根据 号码 获取 Game 8  颜色类型 默认为 六合彩类型
     *
     * @param bet_ball
     * @return
     */
    private fun ball2ColorType4LHC(ball: String?): Int {
        return when (ball) {
            "03", "3", "04", "4", "09", "9", "10", "14", "15", "20", "25", "26", "31", "36", "37", "41", "42", "47", "48" -> GAME_TYPE_COLOR_BLUE
            "01", "1", "02", "2", "07", "7", "08", "8", "12", "13", "18", "19", "23", "24", "29", "30", "34", "35", "40", "45", "46" -> GAME_TYPE_COLOR_RED
            else -> GAME_TYPE_COLOR_GREEN//"05", "5", "06", "6", "11", "16", "17", "21", "22", "27", "28", "32", "33", "38", "39", "43", "44", "49"
        }
    }

    /**
     * 根据号码 获取Game 8 类型下的 玩法ID为74的 颜色类型
     *
     * @param bet_ball
     * @return
     */
    private fun ball2ColorType4LHCByDivide(ball: String?): Int {
        return when ((ball?.toInt() ?: 0) % 3) {
            0 -> GAME_TYPE_COLOR_GREEN
            1 -> GAME_TYPE_COLOR_BLUE
            else -> GAME_TYPE_COLOR_RED
        }
    }
    //endregion

    fun deleteInitZero(ball: String?): String? {
        return if (!ball.isSpace() && ball!!.substring(0, 1)
                .equalsNSpace("0")
        ) ball.substring(1) else ball
    }
}