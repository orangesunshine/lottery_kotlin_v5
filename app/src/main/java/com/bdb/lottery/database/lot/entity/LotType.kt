package com.bdb.lottery.database.lot.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LotteryType")
data class LotType(
    @PrimaryKey val row_id: Int?,
    val name: String,
    val default_sub_id: Int,
)