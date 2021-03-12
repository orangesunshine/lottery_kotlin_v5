package com.bdb.lottery.datasource.lot.data.tr

data class TrInitGameData(
    val animalsNum: Map<String,String>,
    val game: Game,
    val singledInfo: SingledInfo,
    val token: String,
    val user: Double,
    val userBonus: Double,
    val userparent: Int
) {
    data class Game(
        val gameGenres: Int,
        val gamePictureUrl: String,
        val kjmaxwinmoney: Double,
        val name: String,
        val numMax: Int,
        val numMin: Int,
        val remarks: String,
        val startExplain: Boolean,
        val startSign: Boolean,
        val sumbigNum: Int
    )

    data class SingledInfo(
        val id: Int,
        val maxmoney: Double,
        val maxmoney2: Double,
        val maxmoney3: Double,
        val maxmoney4: Double,
        val scales: Double,
        val scales2: Double,
        val scales3: Double,
        val scales4: Double
    )
}