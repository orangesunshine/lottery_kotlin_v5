package com.bdb.lottery.datasource.lot

import android.content.Context
import com.bdb.lottery.database.lot.LotDatabase
import com.bdb.lottery.database.lot.entity.LotType

object LotLocalDs {

    fun queryLotType(context: Context): List<LotType> {
        return LotDatabase.getInstance(context).lotTypeDao().queryLotType()
    }
}