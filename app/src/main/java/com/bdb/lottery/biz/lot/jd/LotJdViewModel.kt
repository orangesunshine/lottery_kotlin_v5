package com.bdb.lottery.biz.lot.jd

import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.biz.lot.jd.single.SingleTextWatcher
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.lot.LotLocalDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.SingleBet
import com.bdb.lottery.datasource.lot.data.TouZhuHaoMa
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.SingledInfo
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.gson.Gsons
import com.bdb.lottery.utils.ui.toast.AbsToast
import javax.inject.Inject

class LotJdViewModel @ViewModelInject @Inject constructor(
    private val tCache: TCache,
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    val gameBetTypeDataLd = LiveDataWrapper<GameBetTypeData?>()//玩法接口
    var subPlayMethodLd = LiveDataWrapper<SubPlayMethod?>()

    private var mToken: String? = null

    //region 初始化彩票
    private var mUserBonus: Double = 0.0
    private var mUserRebate: Double = 0.0
    private var mSingledInfo: SingledInfo? = null
    private var mAlreadyInitGame = false
    fun initGame(gameId: Int) {
        lotRemoteDs.initGame(gameId.toString()) {
            mSingledInfo = it?.singledInfo
            mUserBonus = it?.userBonus ?: 0.0
            mUserRebate = it?.user ?: 0.0
            mToken = it?.token
            if (!mAlreadyInitGame) mAlreadyInitGame = true
        }
    }
    //endregion

    //region 获取彩票玩法
    private var mAlreadyGetBetType = false
    fun getBetType(gameId: Int) {
        lotRemoteDs.getBetType(gameId.toString()) {
            gameBetTypeDataLd.setData(it)
            if (!mAlreadyGetBetType) mAlreadyGetBetType = true
        }
    }
    //endregion

    //region 数据库查找玩法说明
    fun getLocalBetType(playId: Int) {
        lotLocalDs.queryBetTypeByPlayId(playId)
            ?.let {
                subPlayMethodLd.setData(if (it.size > 1) {
                    it.last { !it.belongto.contains("PK10") }
                } else {
                    if (!it.isNullOrEmpty()) it.last() else null
                })
            }
    }
    //endregion

    private var lotteryHowToPlayMap: Map<String, Map<String, String>>? = null
    fun cachePreHowToPlay(
        gameType: Int, playId: Int,
        dialogBlock: ((desc: String?) -> Unit)? = null
    ) {
        if (null != lotteryHowToPlayMap) {
            dialogBlock?.invoke(lotteryHowToPlayMap2Desc(gameType, playId, lotteryHowToPlayMap))
        } else {
            lotRemoteDs.cachePreHowToPlay {
                it?.let {
                    lotteryHowToPlayMap =
                        Gsons.fromJsonByTokeType<Map<String, Map<String, String>>>(it)
                    dialogBlock?.invoke(
                        lotteryHowToPlayMap2Desc(
                            gameType, playId,
                            lotteryHowToPlayMap
                        )
                    )
                }
            }
        }
    }

    private fun lotteryHowToPlayMap2Desc(
        gameType: Int,
        playId: Int,
        lotteryHowToPlayMap: Map<String, Map<String, String>>?
    ): String? {
        if (lotteryHowToPlayMap.isNullOrEmpty()) return null
        val ctMap = lotteryHowToPlayMap["ct"]
        if (ctMap.isNullOrEmpty()) return null
        val key = "gameKind${gameType}_$playId"
        return ctMap[key] ?: ctMap[playId.toString()]
    }

    //region 金额单位
    fun initAmountUnit(gameId: Int, playId: Int, amountUnit: (Int?) -> Unit) {
        amountUnit.invoke(moneyUnitCache(gameId, playId))
    }

    fun cacheMoneyUnit(
        gameId: Int, playId: Int, moneyUnit: Int
    ) {
        tCache.cacheMoneyUnit(gameId, playId, moneyUnit)
    }

    private fun moneyUnitCache(gameId: Int, playId: Int): Int? {
        return tCache.moneyUnitCache(gameId, playId)
    }
    //endregion

    //region 根据玩法配置获取当前位置
    fun getDigit(needDigit: Boolean?, digit: String): String {
        return if (needDigit == true) digit else subPlayMethodLd.getLiveData().value?.subPlayMethodDesc?.digit
            ?: ""
    }
    //endregion

    fun verifyNdGenLotParams(
        gameId: Int,
        playId: Int,
        gameName: String?,
        betNums: String,//投注号码
        multiple: Int,//倍数
        moneyUnit: Int,//金额单位
        digit: String,//用户选中digit
        needDigit: Boolean?,//最少位置
        verifyDigit: String?,//验证位置
        noteCount: Int,//注数
        betItem: BetItem?,
        toast: AbsToast,
        lot: (lotParam: LotParam, refreshToken: (String) -> Unit) -> Unit,
    ) {
        if (!mAlreadyGetBetType) {
            getBetType(gameId)
            toast.showWarning("正在获取玩法配置")
            return
        }

        if (!mAlreadyInitGame) {
            initGame(gameId)
            toast.showWarning("正在初始化彩票")
            return
        }

        //验证digit，选中位置个数
        if (needDigit == true && !verifyDigit.isSpace()) {
            toast.showError(verifyDigit)
            return
        }

        //验证注数大于0
        if (noteCount < 1) {
            toast.showError("请按玩法规则进行投注")
            return
        }

        val touZhuHaoMa = genTouZhuHaoMa(
            playId,
            betNums,
            multiple,
            moneyUnit,
            digit,
            verifyDigit,
            needDigit,
            noteCount,
            getAmount(noteCount, moneyUnit, multiple),
            getSingleMoney(betItem),
            betItem,
            mSingledInfo,
            toast
        ) ?: return
        lot.invoke(
            LotParam(
                gameId.toString(), gameName, null,
                kg = false,
                tingZhiZhuiHao = true,
                token = mToken!!,
                touZhuHaoMa = listOf(touZhuHaoMa),
                zhuiHaoQiHao = ArrayList()
            )
        ) { mToken = it }
    }

    //总额
    fun getAmount(noteCount: Int, moneyUnit: Int, multiple: Int): Double {
        return 2 * noteCount * multiple * Math.pow(10.0, (1 - moneyUnit).toDouble())
    }

    //理论最高奖金
    fun getSingleMoney(
        betItem: BetItem?,
        userRebate: Double = mUserRebate,
        amountModelValue: Double = 1.0,
    ): Double {
        val baseScale = betItem?.baseScale ?: 0.0
        val scale = userRebate * (betItem?.multiple ?: 0.0)
        return (baseScale + scale) * amountModelValue
    }

    private fun genTouZhuHaoMa(
        playId: Int,
        betNums: String,
        multiple: Int,
        moneyUnit: Int,
        digit: String,//用户选中digit
        verifyDigit: String?,//验证位置
        needDigit: Boolean?,//最少位置
        noteCount: Int,
        amount: Double,
        singleMoney: Double,
        betItem: BetItem?,
        singledInfo: SingledInfo?,
        toast: AbsToast,
    ): TouZhuHaoMa? {
        //验证digit，选中位置个数
        if (needDigit == true && !verifyDigit.isSpace()) {
            toast.showError(verifyDigit)
            return null
        }
        val isDanTiao = isDanTiao(noteCount, betItem)
        return TouZhuHaoMa(
            baseScale = singleMoney * multiple,
            singleMoney = singleMoney,
            bouse = mUserBonus,
            danZhuJinEDanWei = Converts.unit2Params(moneyUnit),
            digit = digit,
            model = Converts.unit2Enum(moneyUnit).toString(),
            playtypename = betItem?.getPlayTitle(),
            touZhuBeiShu = multiple,
            yongHuSuoTiaoFanDian = 0.0,
            zhuShu = noteCount,
            wanFaID = playId,
            amount = amount,
            touZhuHaoMa = betNums,
            isDanTiao = isDanTiao,
            singleBet = SingleBet(
                isDanTiao,
                if (isDanTiao) getDanTiaoMaxMoney(betItem, singledInfo) else 0.0
            )
        )
    }

    //region 单挑
    //判断是不是单挑
    private fun isDanTiao(
        noteCount: Int,
        betItem: BetItem?,
    ): Boolean {
        val singleMaxBetCount = betItem?.singleMaxBetCount ?: 0
        return singleMaxBetCount > noteCount
    }

    fun danTiaoTips(betItem: BetItem?): String? {
        if (null == betItem || null == mSingledInfo) return null
        return danTiaoTips(betItem, mSingledInfo!!)
    }

    private fun danTiaoTips(betItem: BetItem, singledInfo: SingledInfo): String {
        val maxBetCount = betItem.maxBetCount
        var scale = 0.0
        var maxMoney = 0.0
        if (maxBetCount >= 10000) {
            scale = singledInfo.scales
            maxMoney = singledInfo.maxmoney
        } else if (maxBetCount in 1000..9999) {
            scale = singledInfo.scales2
            maxMoney = singledInfo.maxmoney2
        } else if (maxBetCount in 100..999) {
            scale = singledInfo.scales3
            maxMoney = singledInfo.maxmoney3
        } else if (maxBetCount in 10..99) {
            scale = singledInfo.scales4
            maxMoney = singledInfo.maxmoney4
        }
        val sb = StringBuffer()
        sb.append("该玩法下注注数小于最高注数的")
        sb.append(scale)
        sb.append("%即为单挑,最高中奖额为")
        sb.append(maxMoney)
        sb.append("元")
        return sb.toString()
    }

    private fun getDanTiaoMaxMoney(
        betItem: BetItem?,
        singledInfo: SingledInfo?,
    ): Double {
        var maxMoney = 0.0
        if (singledInfo != null && betItem != null) {
            val maxBetCount: Int = betItem.maxBetCount
            if (maxBetCount >= 10000) {
                maxMoney = singledInfo.maxmoney
            } else if (maxBetCount in 1000..9999) {
                maxMoney = singledInfo.maxmoney2
            } else if (maxBetCount in 100..999) {
                maxMoney = singledInfo.maxmoney3
            } else if (maxBetCount in 10..99) {
                maxMoney = singledInfo.maxmoney4
            }
        }
        return maxMoney
    }
    //endregion

    fun createSingleTextWatcher(
        gameType: Int,
        playId: Int,
        singleInputEt: EditText,
        singleNumCount: Int,
        digit: String,
        noteCountBlock: (Int) -> Unit,
        error: (String?) -> Unit,
    ): SingleTextWatcher {
        return SingleTextWatcher(
            singleInputEt,
            singleNumCount,
            gameType,
            playId,
            digit,
            noteCountBlock, error
        )
    }
}