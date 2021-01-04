package com.bdb.lottery.utils.lot

import android.text.TextUtils
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.isSpace
import kotlin.math.max
import kotlin.math.min

object Lots {
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
}