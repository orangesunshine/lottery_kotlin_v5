package com.bdb.lottery.datasource.account.data

data class UserInfoData(
    val bonus: Double,
    val email: String,
    val floor: Int,
    val hasWallerPwd: Boolean,
    val id: Int,
    val isCashCard: Boolean,
    val isLockDevice: Boolean,
    val isOpenAG: Int,
    val isOpenAVIA: Any,
    val isOpenBBIN: Int,
    val isOpenBG: Int,
    val isOpenIM: Any,
    val isOpenIMONE: Int,
    val isOpenKX: Int,
    val isOpenKY: Int,
    val isOpenKg: Boolean,
    val isOpenLK: Int,
    val isOpenMG: Int,
    val isOpenPT: Int,
    val isOpenSB: Int,
    val isOpenVR: Int,
    val isOpenXJ: Int,
    val isSeeGuide: Int,
    val isShare: Boolean,
    val isShiWan: Boolean,
    val lastDt: String,
    val loginDt: String,
    val lotteryFavorites: Any,
    val msgCount: Int,
    val nickName: String,
    val otherBonus: OtherBonus,
    val qq: String,
    val queryUserName: String,
    val regdt: String,
    val shareStatus: String,
    val signUserRecordMap: SignUserRecordMap,
    val telephone: String,
    val transferAccounts: Int,
    val transferpower: String,
    val userHeadImg: String,
    val userMinRebate: Double,
    val userName: String,
    val userPersonalise: UserPersonalise?,
    val userRebateLeve: Int,
    val userType: Int,
    val vipUserRecordMap: VipUserRecordMap,
    val walletBal: Double
)

data class OtherBonus(
    val agBonus: String,
    val bbinBonus: String,
    val bgBonus: String,
    val cpBonus: String,
    val kgBonus: String,
    val kxBonus: String,
    val kyBonus: String,
    val lkBonus: String,
    val mgBonus: String,
    val ptBonus: String,
    val sbBonus: String,
    val vrBonus: String,
    val xjBonus: String
)

data class SignUserRecordMap(
    val isReceive: Int,
    val isSign: Int
)

data class UserPersonalise(
    val ctBetChip: String,
    val freeTransfer: Boolean,
    val numMemory: Boolean,
    val ptBetGame: Int,
    val ptBetPattern: String,
    val ptBetPlay: Int,
    val ptBetType: Int,
    val reserve: Any,
    val reserve1: Any,
    val reserve2: Any,
    val reserve3: Any,
    val userId: Int,
    val userName: String,
    val voiceStatus: Boolean,
    val winPush: Boolean
)

data class VipUserRecordMap(
    val exp: Double,
    val level: Int,
    val nobilityId: Int,
    val nobilityName: String,
    val rewardStatus: Int
)
