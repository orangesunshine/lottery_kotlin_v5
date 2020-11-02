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
)