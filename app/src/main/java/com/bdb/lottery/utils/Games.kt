package com.bdb.lottery.utils

import com.bdb.lottery.R

object Games {
    const val GAMEKIND_SSC = 1 //时时彩

    const val GAMEKIND_SYX5 = 3 //十一选5

    const val GAMEKIND_PK10 = 5 //PK10

    const val GAMEKIND_PLS_FC3D = 2 //排列三，福彩3D

    const val GAMEKIND_KS = 7 //快三

    const val GAMEKIND_KLSF = 9 //快乐10分

    const val GAMEKIND_PCDD = 6 //香港六合彩

    const val GAMEKIND_XGLHC = 8 //香港六合彩


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