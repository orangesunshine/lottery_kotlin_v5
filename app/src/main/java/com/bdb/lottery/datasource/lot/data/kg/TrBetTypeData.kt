package com.bdb.lottery.datasource.lot.data.kg

class TrBetTypeData : ArrayList<TrBetTypeData.TrBetTypeGroupData>() {
    data class TrBetTypeGroupData(
        val list: List<TrBetTypeSubGroupData>,
        val name: String,
        val ranking: Int
    ) {
        data class TrBetTypeSubGroupData(
            val list: List<TrBetTypeDataItem>,
            val name: String,
            val ranking: Int
        ) {
            data class TrBetTypeDataItem(
                val classify: String,
                val digit: String,
                val id: Int,
                val maxbet: Double,
                val minbet: Double,
                val name: String,
                val odds: Double,
                val playTypeId: Int,
                val playTypeName: String,
                val ranking: Int,
                val sixplaytype: Int
            )
        }
    }
}