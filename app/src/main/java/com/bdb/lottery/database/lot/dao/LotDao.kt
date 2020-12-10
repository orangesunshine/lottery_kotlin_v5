package com.bdb.lottery.database.lot.dao

import androidx.room.Dao
import androidx.room.Query
import com.bdb.lottery.database.lot.entity.LotType

@Dao
interface LotTypeDao {
    @Query("select * from LotteryType")
    fun queryLotType(): List<LotType>
}