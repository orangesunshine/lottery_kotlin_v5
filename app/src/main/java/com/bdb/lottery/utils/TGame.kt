package com.bdb.lottery.utils

import com.bdb.lottery.R
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_KLSF
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_KS
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_PCDD
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_PK10
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_PLS_FC3D
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_SSC
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_SYX5
import com.bdb.lottery.const.IGame.Companion.GAMEKIND_XGLHC
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
            GAMEKIND_PCDD -> drawable =
                R.drawable.home_allgame_type_pcdd
        }
        return drawable
    }
}