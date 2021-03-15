package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.R
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.biz.lot.BetCenter
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexLd
import com.bdb.lottery.biz.lot.jd.single.SingleTextWatcher
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.const.GAME
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.lot.LotLocalDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.SingleBet
import com.bdb.lottery.datasource.lot.data.TouZhuHaoMa
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.OddInfo
import com.bdb.lottery.datasource.lot.data.jd.SingledInfo
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.isDigit
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.gson.Gsons
import com.bdb.lottery.utils.ui.toast.AbsToast
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class LotJdViewModel @ViewModelInject @Inject constructor(
    private val tCache: TCache,
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {

    //region 请求数据

    //region 彩票初始化信息：网络请求
    private var mToken: String? = null
    private var mUserBonus: Double = 0.0
    private var mUserRebate: Double = 0.0//三方游戏返点
    private var mSingledInfo: SingledInfo? = null//最高限红
    private var mOddInfo: List<OddInfo>? = null//理论最高奖金
    private var mAlreadyInitGame = AtomicBoolean()
    fun netInitGame() {
        lotRemoteDs.initGame(mGameId.toString()) {
            mAlreadyInitGame.set(true)
            mSingledInfo = it.singledInfo
            mOddInfo = it.oddInfo
            mUserBonus = it.userBonus
            mUserRebate = it.user
            mToken = it.token
        }
    }
    //endregion

    //region 彩票玩法配置信息：网络请求
    private var mAlreadyGetBetType = AtomicBoolean()
    val gameBetTypeDataLd = LiveDataWrapper<GameBetTypeData?>()//玩法接口
    fun netBetType() {
        lotRemoteDs.getBetType(mGameId.toString()) {
            gameBetTypeDataLd.setData(it)
            mAlreadyGetBetType.set(true)
        }
    }
    //endregion

    //region 玩法配置：本地数据库
    var singleModeLd = LiveDataWrapper<Boolean>()//默认复式
    var singleNumCountsLd = LiveDataWrapper<Int>()//单式切换玩法：一注号码球个数

    val atLeastDigitLd = LiveDataWrapper<Int>()//最少digit位数
    val needDigitLd = LiveDataWrapper<Boolean>()//是否显示digit
    val parentPlayMethodLd = LiveDataWrapper<Int>()//开奖历史形态
    val duplexBallDatasLd = LiveDataWrapper<LotDuplexLd>()//复式玩法切换
    private var mDigits: String? = null
    private var mIsBuildAll: Boolean? = null
    private var mDigitTitles: String? = null
    fun dbBetType(betTypeId: Int) {
        mBetTypeId = betTypeId
        lotLocalDs.queryBetTypeByPlayId(betTypeId)
            ?.let {
                val subPlayMethod = if (it.size > 1) {
                    it.last { !it.belongto.contains("PK10") }
                } else {
                    if (!it.isNullOrEmpty()) it.last() else null
                }
                subPlayMethod?.let { it ->
                    mDigits = it.subPlayMethodDesc.digit

                    singleModeLd.setData(it.subPlayMethodDesc.isdanshi)
                    if (isSingleMode()) {
                        singleNumCountsLd.setData(it.subPlayMethodDesc.single_num_counts)
                    } else {
                        mIsBuildAll = it.subPlayMethodDesc.isBuildAll
                        mDigitTitles = it.subPlayMethodDesc.digit_titles
                        duplexBallDatasLd.setData(
                            LotDuplexLd(
                                mGameType,
                                mBetTypeId,
                                mIsBuildAll,
                                mDigitTitles,
                                getBallTextList(it.subPlayMethodDesc.ball_text_list),
                                genDuplexDatas(it)
                            )
                        )
                    }
                    it.subPlayMethodDesc.atleast_wei_shu?.let { atLeastDigitLd.setData(it.toInt()) }
                    needDigitLd.setData(it.subPlayMethodDesc.is_need_show_weizhi)
                    parentPlayMethodLd.setData(it.parent_play_method)
                }
            }
    }
    //endregion

    //region 玩法说明：缓存优先--请求js网络数据
    private var lotteryHowToPlayMap: Map<String, Map<String, String>>? = null
    fun cachePreHowToPlay(dialogBlock: ((gameType: Int, descText: String?) -> Unit)? = null) {
        if (null != lotteryHowToPlayMap) {
            dialogBlock?.invoke(mGameType, lotteryHowToPlayMap2Desc(lotteryHowToPlayMap))
        } else {
            lotRemoteDs.cachePreHowToPlay {
                it?.let {
                    lotteryHowToPlayMap =
                        Gsons.fromJsonByTokeType<Map<String, Map<String, String>>>(it)
                    dialogBlock?.invoke(mGameType, lotteryHowToPlayMap2Desc(lotteryHowToPlayMap))
                }
            }
        }
    }
    //endregion
    //endregion

    //region 初始化参数：倍数、彩种id、彩种大类，彩种名称，玩法id
    private var mGameType: Int = -1
    private var mGameId: Int = -1
    private var mBetTypeId: Int = -1
    private var mGameName: String? = null
    fun initExtraArgs(args: Bundle?) {
        args?.let {
            mGameType = it.getInt(EXTRA.TYPE_GAME_EXTRA)
            mGameId = it.getInt(EXTRA.ID_GAME_EXTRA)
            mBetTypeId = it.getInt(EXTRA.ID_BET_TYPE_EXTRA)
            mGameName = it.getString(EXTRA.NAME_GAME_EXTRA)
        }
    }
    //endregion

    //region 判断快三：彩种大类
    fun isK3(): Boolean {
        return GAME.TYPE_GAME_K3 == mGameType
    }
    //endregion

    //region 金额单位
    //缓存金额单位
    fun cacheMoneyUnit(moneyUnit: Int) {
        tCache.cacheMoneyUnit(mGameId, mBetTypeId, moneyUnit)
    }

    //金额单位缓存
    fun moneyUnitCache(): Int {
        return tCache.moneyUnitCache(mGameId, mBetTypeId) ?: 1
    }
    //endregion

    //region 计算注数--复式
    fun computeDuplexCount(
        numsText: String,
        selectedDigit: String,
    ): Int {
        return BetCenter.computeStakeCount(
            numsText,
            mGameType,
            mBetTypeId,
            getDigit(selectedDigit)
        )
    }
    //endregion

    //region 号码球生成字符串--复式
    fun makeBetNumStr(betNums: List<List<String?>?>?): String {
        return BetCenter.makeBetNumStr(betNums, mIsBuildAll, mDigitTitles)
    }
    //endregion

    //region 是否显示digit
    fun needDigit(): Boolean {
        return needDigitLd.getData() == true
    }
    //endregion

    //region 至少几位digit
    fun atLeastDigit(): Int? {
        return atLeastDigitLd.getData()
    }
    //endregion

    //region 是否单式
    fun isSingleMode(): Boolean {
        return singleModeLd.getData() == true
    }
    //endregion

    //region 颜色资源：换肤-投注区域、底部投注信息栏
    fun betAreaBgResSkinByGameType(): Int {
        return when (mGameType) {
            GAME.TYPE_GAME_PK8, GAME.TYPE_GAME_PK10 -> R.color.color_skin_pk_bg_jd
            GAME.TYPE_GAME_K3 -> R.color.color_skin_k3_bg_jd
            else -> R.color.color_bg
        }
    }
    //endregion

    //region 颜色资源：换肤-投注区域顶部分割线
    fun betAreaTopDivideSkinByGameType(): Int {
        return when (mGameType) {
            GAME.TYPE_GAME_PK8, GAME.TYPE_GAME_PK10 -> R.color.color_play_desc_skin_pk_divide
            GAME.TYPE_GAME_K3 -> R.color.color_play_desc_skin_k3_divide
            else -> R.color.color_play_desc_skin_divide
        }
    }
    //endregion

    //region 玩法digits
    fun getDigit(selectedDigit: String): String {
        return if (needDigit()) selectedDigit else mDigits ?: ""
    }
    //endregion

    //region 校验下注参数
    fun verifyNdGenLotParams(
        betNums: String,//投注号码
        multiple: Int,//倍数
        moneyUnit: Int,//金额单位
        digit: String,//用户选中digit
        verifyDigit: String?,//验证位置
        noteCount: Int,//注数
        betItem: BetItem?,
        toast: AbsToast,
        needDigit: Boolean? = needDigit(),//最少位置
        lot: (lotParam: LotParam, refreshToken: (String) -> Unit) -> Unit,
    ) {
        if (!mAlreadyGetBetType.get()) {
            netBetType()
            toast.showWarning("正在获取玩法配置")
            return
        }

        if (!mAlreadyInitGame.get()) {
            netInitGame()
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
            mBetTypeId,
            betNums,
            multiple,
            moneyUnit,
            digit,
            verifyDigit,
            needDigit,
            noteCount,
            getAmount(noteCount, moneyUnit, multiple),
            getSingleMoney(betNums, betItem),
            betItem,
            mSingledInfo,
            toast
        ) ?: return
        lot.invoke(
            LotParam(
                mGameId.toString(), mGameName, null,
                kg = false,
                tingZhiZhuiHao = true,
                token = mToken!!,
                touZhuHaoMa = listOf(touZhuHaoMa),
                zhuiHaoQiHao = ArrayList()
            )
        ) { mToken = it }
    }
    //endregion

    //region 总额
    fun getAmount(noteCount: Int, moneyUnit: Int, multiple: Int): Double {
        return 2 * noteCount * multiple * Math.pow(10.0, (1 - moneyUnit).toDouble())
    }
    //endregion

    //region 理论最高奖金
    fun getSingleMoney(
        betNums: String?,
        betItem: BetItem?,
        userRebate: Double = mUserRebate,
        amountModelValue: Double = 1.0,
    ): Double {
        if (isLHH(betNums)) {
            mOddInfo?.let {
                for (oddInfo in it) {
                    if (oddInfo.special_number.equalsNSpace(betNums)) return BetCenter.computeBonusText(
                        oddInfo,
                        mUserBonus
                    )
                }
            }
            return 0.0
        } else {
            val baseScale = betItem?.baseScale ?: 0.0
            val scale = userRebate * (betItem?.multiple ?: 0.0)
            return (baseScale + scale) * amountModelValue
        }
    }

    /**
     * 根据彩种大类，投注号码判断是否使用龙虎和奖金(initGame->oddInfo)
     */
    private fun isLHH(betNums: String?): Boolean {
        if (isSingleMode()) return false//单式
        return when (mGameType) {
            GAME.TYPE_GAME_SSC -> return StringUtils.equalsAny(betNums, "龙", "虎", "和")
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> return StringUtils.equalsAny(
                betNums,
                "大小单双",
                "和值"
            )
            GAME.TYPE_GAME_K3 -> return StringUtils.equals(betNums, "和值")
            else -> false
        }
    }
    //endregion

    //region 单挑提示语：下注弹窗
    fun danTiaoTips(betItem: BetItem?): String? {
        if (null == betItem || null == mSingledInfo) return null
        return danTiaoTips(betItem, mSingledInfo!!)
    }
    //endregion

    //region 投注号码输入框监听--单式
    fun createSingleTextWatcher(
        singleInputEt: EditText,
        singleNumCount: Int,
        digit: String,
        noteCountBlock: (Int) -> Unit,
        error: (String?) -> Unit,
    ): SingleTextWatcher {
        return SingleTextWatcher(
            singleInputEt,
            singleNumCount,
            mGameType,
            mBetTypeId,
            digit,
            noteCountBlock, error
        )
    }
    //endregion

    //region 工具方法

    //非数字球
    private fun getBallTextList(ballTextList: String?): List<String>? {
        return ballTextList?.split(",")?.let {
            if (GAME.TYPE_GAME_K3 == mGameType) it.sortedWith { s: String, s1: String ->
                val c1 = if (s.isDigit()) 1 else -1
                val c2 = if (s1.isDigit()) 1 else -1
                c1 - c2
            } else it
        }
    }

    //复式：复式号码球数据
    private fun genDuplexDatas(subPlayMethod: SubPlayMethod): MutableList<LotDuplexData> {
        val lotDuplexDatas = mutableListOf<LotDuplexData>()
        val ballGroupsCounts = subPlayMethod.subPlayMethodDesc.ball_groups_counts
        val titles = subPlayMethod.subPlayMethodDesc?.ball_groups_item_title?.split(",")
        if (ballGroupsCounts > 0) {
            for (i in 0 until ballGroupsCounts) {
                lotDuplexDatas.add(
                    LotDuplexData(
                        titles?.get(i),
                        subPlayMethod.subPlayMethodDesc.item_ball_num_counts,
                        subPlayMethod.subPlayMethodDesc.ball_text_list?.split(",").let {
                            if (isK3()) it?.sortedWith { s: String, s1: String ->
                                val c1 = if (s.isDigit()) 1 else -1
                                val c2 = if (s1.isDigit()) 1 else -1
                                c1 - c2
                            } else it
                        },
                        dxdsVisible = subPlayMethod.subPlayMethodDesc.is_show_type_select,
                        hotVisible = false,
                        leaveVisible = false,
                        isStartZero = subPlayMethod.subPlayMethodDesc.is_start_zero,
                        zeroVisible = subPlayMethod.subPlayMethodDesc.isShowZero,
                    )
                )
            }
        }
        return lotDuplexDatas
    }

    //玩法说明
    private fun lotteryHowToPlayMap2Desc(
        lotteryHowToPlayMap: Map<String, Map<String, String>>?,
    ): String? {
        if (lotteryHowToPlayMap.isNullOrEmpty()) return null
        val ctMap = lotteryHowToPlayMap["ct"]
        if (ctMap.isNullOrEmpty()) return null
        val key = "gameKind${mGameType}_$mBetTypeId"
        return ctMap[key] ?: ctMap[mBetTypeId.toString()]
    }

    //投注参数：touZhuHaoMa
    private fun genTouZhuHaoMa(
        mPlayId: Int,
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
            playtypename = betItem?.getJdPlayTitle(),
            touZhuBeiShu = multiple,
            yongHuSuoTiaoFanDian = 0.0,
            zhuShu = noteCount,
            wanFaID = mPlayId,
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

    //单挑判断
    private fun isDanTiao(
        noteCount: Int,
        betItem: BetItem?,
    ): Boolean {
        val singleMaxBetCount = betItem?.singleMaxBetCount ?: 0
        return singleMaxBetCount > noteCount
    }

    //单挑提示语
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

    //单挑--最大限红
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
    //endregion
}