package com.bdb.lottery.utils

import com.bdb.lottery.R
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_KLSF
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_KS
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_PC28
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_PK10
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_PLS_FC3D
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_SSC
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_SYX5
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_XGLHC
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.isSpace
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
            GAMEKIND_SSC -> drawable =
                R.drawable.home_allgame_type_ssc
            GAMEKIND_SYX5 -> drawable =
                R.drawable.home_allgame_type_11x5
            GAMEKIND_PK10 -> drawable =
                R.drawable.home_allgame_type_pk10
            GAMEKIND_PLS_FC3D -> drawable =
                R.drawable.home_allgame_type_lows
            GAMEKIND_KS -> drawable =
                R.drawable.home_allgame_type_k3
            GAMEKIND_KLSF -> drawable =
                R.drawable.home_allgame_type_klc
            GAMEKIND_XGLHC -> drawable =
                R.drawable.home_allgame_type_lhc
            GAMEKIND_PC28 -> drawable =
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
            val isPc28 = gameType.equalsNSpace(GAMEKIND_PC28.toString())
            return if (issue.length > 8 && !isPc28) issue.substring(4) else issue
        }
    }
}