package com.bdb.lottery.datasource.game.data

data class AllGameDataItemData(
    var deep: Int,
    var gameId: Int,
    var gameKind: Int,
    var hotSort: Int,
    var kgEnable: Boolean,
    var name: String,
    var numMax: Int,
    var numMin: Int,
    var ptEnable: Boolean,
    var recommendSort: Int,
    var recommendType: String,
    var remark: String?,
    var sumbigNum: Int,
    var wtEnable: Boolean
) {
    var gameType: Int = gameKind
        set(value) {
            field = value
        }
        get() {
            return if (11 == gameKind) 5 else gameKind
        }

}

class AllGameDataMapper() {
    var leftGameType: Int = -1
    var leftDatas: List<AllGameDataItemData>? = null
    var rightGameType: Int = -1
    var rightDatas: List<AllGameDataItemData>? = null
}