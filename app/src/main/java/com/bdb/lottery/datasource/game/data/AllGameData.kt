package com.bdb.lottery.datasource.game.data

data class AllGameItemData(
    var deep: Int,
    var gameId: Int?,
    var gameKind: Int?,
    var hotSort: Int,
    var kgEnable: Boolean,
    var name: String,
    var numMax: Int,
    var numMin: Int,
    var ptEnable: Boolean,
    var recommendSort: Int,
    var recommendType: String?,
    var remark: String?,
    var sumbigNum: Int,
    var wtEnable: Boolean,
) {
    val gameType: Int?
        get() {
            return if (11 == gameKind) 5 else gameKind
        }
    var imgUrl = ""
}

class AllGameDataMapper() {
    var leftGameType: Int? = -1
    var leftData: List<AllGameItemData>? = null
    var rightGameType: Int? = -1
    var rightData: List<AllGameItemData>? = null
    override fun toString(): String {
        return "AllGameDataMapper(leftGameType=$leftGameType, leftData=$leftData, rightGameType=$rightGameType, rightData=$rightData)"
    }
}