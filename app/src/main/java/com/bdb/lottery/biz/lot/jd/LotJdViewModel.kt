package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotLocalDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.GameInitData
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer1Item
import com.bdb.lottery.datasource.lot.data.jd.PlayLayer2Item
import com.bdb.lottery.utils.cache.TCache
import javax.inject.Inject

class LotJdViewModel @ViewModelInject @Inject constructor(
    private val tCache: TCache,
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    var mToken: String? = null
    val mGameInitData = LiveDataWraper<GameInitData?>()
    val mGameBetTypeData = LiveDataWraper<GameBetTypeData?>()
    val subPlayMethod = LiveDataWraper<SubPlayMethod?>()

    //region 初始化彩票
    fun initGame() {
        lotRemoteDs.initGame(mGameId.toString()) {
            mGameInitData.setData(it)
            mToken = it?.token
        }
    }
    //endregion

    //region 获取彩票玩法
    fun getBetType() {
        lotRemoteDs.getBetType(mGameId.toString()) {
            mGameBetTypeData.setData(it)
        }
    }
    //endregion

    //region 数据库查找玩法说明
    fun getLocalBetType(playId: Int) {
        subPlayMethod.setData(lotLocalDs.queryBetTypeByPlayId(playId)
            ?.let {
                if (it.size > 1) {
                    it.last { !it.belongto.contains("PK10") }
                } else {
                    if (!it.isNullOrEmpty()) it.last() else null
                }
            })
    }
    //endregion

    //region 删除重复号码
    var mSingleNumCount: Int = 5//单注号码数

    var mNoteCount: Int = 0//投注注数

    fun repeatNdErrorNums(text: String): String? {
        mNoteCount = 0
        if (text.length <= mSingleNumCount) return null
        val buff = StringBuilder(text)
        var offset = mSingleNumCount
        while (offset < buff.length && offset > 0) {
            buff.insert(offset, ",")
            offset += 1 + mSingleNumCount
            mNoteCount++
        }
        return buff.toString()
    }
    //endregion

    //region 初始化彩票参数、玩法下标
    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    fun initExtraVar(arguments: Bundle?) {
        arguments?.let {
            mGameId = it.getInt(EXTRA.ID_GAME_EXTRA)
            mGameType = it.getInt(EXTRA.TYPE_GAME_EXTRA)
            mGameName = it.getString(EXTRA.NAME_GAME_EXTRA)
        }
        playCacheByGameId(mGameId)
    }
    //endregion

    //region 经典：玩法下标-缓存初始化
    private var mPlaySelectedPos: Int = 0
    private var mGroupSelectedPos: Int = 0
    private var mBetSelectedPos: Int = 0
    private var mPlayId: Int = 0
    fun renderPlayNdBet(
        play: (playSelectedPos: Int, betSelectedPos: Int) -> Unit
    ) {
        play.invoke(mPlaySelectedPos, mBetSelectedPos)
    }

    private fun playCacheByGameId(gameId: Int) {
        mPlaySelectedPos = tCache.playLayer1Cache4GameId(gameId)
        mGroupSelectedPos = tCache.playGroupCache4GameId(gameId)
        mBetSelectedPos = tCache.playLayer2Cache4GameId(gameId)
        mPlayId = tCache.playIdCache4GameId(gameId)
    }

    fun cachePlay4GameId() {
        tCache.cachePlay4GameId(
            mGameId,
            mPlaySelectedPos,
            mGroupSelectedPos,
            mBetSelectedPos,
            mPlayId
        )
    }
    //endregion

    //region 判断是不是当前选中的一级玩法
    fun isCurPlayNdGroup(playIndex: Int, groupIndex: Int): Boolean {
        return mPlaySelectedPos == playIndex && mGroupSelectedPos == groupIndex
    }
    //endregion

    //region 判断是不是当前选中二级玩法分组
    fun isCurGroup(groupPosition: Int): Boolean {
        return mGroupSelectedPos == groupPosition
    }
    //endregion

    //region 玩法选中
    fun onBetSelectedByClick(
        playSelectedPos: Int,
        groupSelectedPos: Int,
        betSelectedPos: Int,
        unSelectPre: (preGroupPosition: Int) -> Unit
    ) {
        //选中玩法：更新一级玩法下标、二级玩法组下标、二级玩法下标
        if (mPlaySelectedPos == playSelectedPos && mGroupSelectedPos == groupSelectedPos && mBetSelectedPos == betSelectedPos) return
        if (mPlaySelectedPos != playSelectedPos) {
            mPlaySelectedPos = playSelectedPos
        } else {
            if (mGroupSelectedPos != groupSelectedPos) {
                val preGroupPosition = mGroupSelectedPos
                unSelectPre.invoke(preGroupPosition)
            }
        }
        mGroupSelectedPos = groupSelectedPos
        mBetSelectedPos = betSelectedPos
    }

    //初始化玩法选中
    fun onBetSelected(item: PlayLayer1Item?, onBetSelected: (item: PlayLayer2Item?) -> Unit) {
        item?.list?.let {
            if (mGroupSelectedPos < it.size) {
                it.get(mGroupSelectedPos).list?.let {
                    if (mBetSelectedPos < it.size) {
                        onBetSelected.invoke(it[mBetSelectedPos])
                    }
                }
            }
        }
    }
    //endregion

    //region 金额单位
    var mAmountUnit: Int = 1//默认元
    fun setAmountUnit(unit: Int) {
        mAmountUnit = unit
    }
    //endregion
}