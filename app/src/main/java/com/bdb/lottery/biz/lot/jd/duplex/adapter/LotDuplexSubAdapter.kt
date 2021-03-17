package com.bdb.lottery.biz.lot.jd.duplex.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.*
import com.bdb.lottery.module.application.AppEntries
import com.bdb.lottery.utils.game.Games
import com.bdb.lottery.utils.lot.Lots
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.util.getItemView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import dagger.hilt.android.EntryPointAccessors
import org.apache.commons.lang3.StringUtils

class LotDuplexSubAdapter(
    private var singleClick: Boolean = false,
    private val gameType: Int,
    private var betTypeId: Int,
    private var spanCount: Int,
    private var lotLotDuplexData: LotDuplexData,
    var oddInfoMap: Map<String, Double>?,
    var hot: List<Int>?,
    var leave: List<Int>?,
    updateBlock: (list: ArrayList<Int>) -> Unit,
) : BaseSelectedQuickAdapter<String, BaseViewHolder>(
    R.layout.lot_duplex_sub_item,
    lotLotDuplexData.genBallDatas(), updateBlock
) {
    companion object {
        const val PAY_LOADS_HOT_LEAVE_VISIBLE = "PAY_LOADS_HOT_LEAVE"
        const val PAY_LOAD_ODD_INFO = "PAY_LOAD_ODD_INFO"
        const val PAY_LOAD_HOT = "PAY_LOAD_HOT"
        const val PAY_LOAD_LEAVE = "PAY_LOAD_LEAVE"
    }

    private var isk3HeZhi = Lots.isK3HeZhi(betTypeId)//快三和值特殊处理（同时数字球，非数字球）：大小单上一行4列，数字号码一行6列

    private val tSound by lazy {
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTSound()
    }

    //region 玩法改变刷新
    fun notifyChangeWhenPlayChange(
        singleClick: Boolean = false,
        betTypeId: Int,
        spanCount: Int,
        lotLotDuplexData: LotDuplexData,
    ) {
        this.singleClick = singleClick
        this.betTypeId = betTypeId
        this.spanCount = spanCount
        this.lotLotDuplexData = lotLotDuplexData
        isk3HeZhi = Lots.isK3HeZhi(betTypeId)
        setNewInstance(lotLotDuplexData.genBallDatas())
    }
    //endregion

    private val LONG_HU_HE_VIEW = 0x10000666//龙虎和单独布局文件
    override fun getItemViewType(position: Int): Int {
        return if (Lots.isLHH(getItem(position))) {
            //龙虎和
            LONG_HU_HE_VIEW
        } else {
            return super.getItemViewType(position)
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (LONG_HU_HE_VIEW == viewType) {
            return createBaseViewHolder(parent.getItemView(R.layout.lot_duplex_sub_item_long_hu))
        } else {
            return super.onCreateDefViewHolder(parent, viewType)
        }
    }

    private val isPk10 = GAME.TYPE_GAME_PK10 == gameType || GAME.TYPE_GAME_PK8 == gameType
    override fun convert(holder: BaseViewHolder, item: String) {
        super.convert(holder, item)
        val itemViewType = holder.itemViewType
        val adapterPosition = holder.adapterPosition
        if (itemViewType == LONG_HU_HE_VIEW) {//龙虎和
            holder.setImageResource(
                R.id.lot_duplex_sub_item_iv, when (item) {
                    "龙" -> if (isPk10) R.drawable.lot_duplex_sub_item_pk_long_selected else R.drawable.lot_duplex_sub_item_long_selector
                    "虎" -> if (isPk10) R.drawable.lot_duplex_sub_item_pk_hu_selected else R.drawable.lot_duplex_sub_item_hu_selector
                    "和" -> R.drawable.lot_duplex_sub_item_he_selector
                    else -> -1
                }
            )
            holder.setText(R.id.lot_duplex_sub_item_tv,
                if (Lots.isLHH(item) && !Games.isPK(gameType)) (oddInfoMap?.get(item)
                    ?: 0.0).format() else getItem(adapterPosition))
            holder.setTextColorStateList(
                context,
                R.id.lot_duplex_sub_item_tv,
                R.color.lot_duplex_sub_item_long_ball_color_selector
            )
        } else {//非龙虎和
            //设置球文字
            holder.setText(
                R.id.lot_duplex_sub_item_tv, getItem(adapterPosition)//数据源已处理过非数字球，这里直接取
            )
            val isK3 = Games.isK3(gameType)
            val containsDxds = StringUtils.containsAny(item, "大", "小", "单", "双")
            //根据gameType设置球选中文字颜色
            holder.setTextColorStateList(
                context, R.id.lot_duplex_sub_item_tv, when (gameType) {
                    GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> R.color.lot_duplex_sub_item_pk_ball_color_selector
                    GAME.TYPE_GAME_K3 -> R.color.lot_duplex_sub_item_k3_ball_color_selector
                    else -> R.color.lot_duplex_sub_item_ball_color_selector
                }
            )
            holder.setTextSize(
                R.id.lot_duplex_sub_item_tv, when (spanCount) {
                    6 -> {
                        if (isK3) 20f else 14f
                    }
                    12 -> {
                        if (isK3 && isk3HeZhi) 16f else 18f
                    }
                    else -> {
                        if (item.length > 1) 15f else 18f
                    }
                }
            )
            when (spanCount) {
                6 ->
                    holder.setWH(
                        R.id.lot_duplex_sub_item_tv, Sizes.dp2px(if (isK3) 40f else 30f)
                    )
                12 ->
                    holder.setWH(//快三宽度铺满，高度45
                        R.id.lot_duplex_sub_item_tv,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Sizes.dp2px(45f)
                    )
                else ->//5列
                    holder.setWH(
                        R.id.lot_duplex_sub_item_tv,
                        Sizes.dp2px(35f)
                    )
            }
            //根据gameType设置球选中背景
            holder.setBackgroundResource(
                R.id.lot_duplex_sub_item_tv, when (gameType) {
                    GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> if (containsDxds) R.drawable.lot_duplex_sub_item_pk_circle_selector else R.drawable.lot_duplex_sub_item_pk_round_selector
                    GAME.TYPE_GAME_K3 -> R.drawable.lot_duplex_sub_item_k3_selector
                    else -> R.drawable.lot_duplex_sub_item_selector
                }
            )
            //false：显示冷热，true：显示遗漏，null：都不显示
            mHotLeaveVisible?.let {
                holder.setText(R.id.lot_duplex_sub_item_hot_tv,
                    ((if (it) leave else hot)?.get(adapterPosition) ?: 0).toString())
                holder.setTextColor(R.id.lot_duplex_sub_item_hot_tv,
                    if (it) leaveTextColor(item, leave) else hotTextColor(item, hot))
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: String, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        val adapterPosition = holder.adapterPosition
        //冷热遗漏可见
        if (PAY_LOADS_HOT_LEAVE_VISIBLE.equalsPayLoads(payloads)) {
            holder.setGone(R.id.lot_duplex_sub_item_hot_tv, null == mHotLeaveVisible)
        }

        if (PAY_LOAD_HOT.equalsPayLoads(payloads)) {
            holder.setText(R.id.lot_duplex_sub_item_hot_tv,
                (hot?.get(adapterPosition) ?: 0).toString())
            holder.setTextColor(R.id.lot_duplex_sub_item_hot_tv, hotTextColor(item, hot))
        }

        if (PAY_LOAD_LEAVE.equalsPayLoads(payloads)) {
            holder.setText(R.id.lot_duplex_sub_item_hot_tv,
                (leave?.get(adapterPosition) ?: 0).toString())
            holder.setTextColor(R.id.lot_duplex_sub_item_hot_tv, leaveTextColor(item, hot))
        }

        //龙虎和奖金
        if (Lots.isLHH(item) && PAY_LOAD_ODD_INFO.equalsPayLoads(payloads)) {
            holder.setText(R.id.lot_duplex_sub_item_tv, (oddInfoMap?.get(item) ?: 0.0).format())
        }
    }

    init {
        //号码球：点击
        setOnItemClickListener { _: BaseQuickAdapter<*, *>, view: View, position: Int ->
            tSound.playBetBallClickSound()//声音
            YoYo.with(Techniques.ZoomIn).duration(100).playOn(view)//小到大动画效果
            notifySelectedPositionWithPayLoads(position, singleSelected = singleClick)//通知选中状态更新
        }
    }

    private fun hotTextColor(num: String, nums: List<Int>?): Int {
        return Color.parseColor(try {
            val toInt = num.toInt()
            if (nums?.maxOrNull() == toInt) "#f9583f" else if (nums?.minOrNull() == toInt) "#4c9ae8" else "#7e7f7f"
        } catch (e: Exception) {
            "#7e7f7f"
        })
    }

    private fun leaveTextColor(num: String, nums: List<Int>?): Int {
        return Color.parseColor(try {
            val toInt = num.toInt()
            if (nums?.maxOrNull() == toInt) "#4c9ae8" else if (nums?.minOrNull() == toInt) "#f9583f" else "#7e7f7f"
        } catch (e: Exception) {
            "#7e7f7f"
        })
    }

    //region 大小单双
    fun selectedList2DxdsTag(list: ArrayList<Int>): Int {
        list.sort()
        return if (list.equals(getBigBallSelectedPositions())) {
            0
        } else if (list.equals(getSmallBallSelectedPositions())) {
            1
        } else if (list.equals(getSingleBallSelectedPositions())) {
            2
        } else if (list.equals(getDoubleBallSelectedPositions())) {
            3
        } else {
            -1
        }
    }

    //region 大
    private fun getBigBallSelectedPositions(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in itemCount / 2 until itemCount) {
            list.add(i)
        }
        return list
    }

    fun bigSelectedChange(selected: Boolean) {
        tSound.playBetBallClickSound()
        if (selected)
            notifyMultiSelectedPositionWithPayLoads(getBigBallSelectedPositions())
        else
            notifyUnSelectedAllWithPayLoads()
    }
    //endregion

    //region 选中小
    private fun getSmallBallSelectedPositions(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in 0 until itemCount / 2) {
            list.add(i)
        }
        return list
    }

    fun smallSelectedChange(selected: Boolean) {
        tSound.playBetBallClickSound()
        if (selected)
            notifyMultiSelectedPositionWithPayLoads(getSmallBallSelectedPositions())
        else
            notifyUnSelectedAllWithPayLoads()
    }
    //endregion

    //region 选中单
    private fun getSingleBallSelectedPositions(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in 0 until itemCount) {
            if (lotLotDuplexData.isStartZero) {
                if (i % 2 != 0)
                    list.add(i)
            } else {
                if (i % 2 == 0)
                    list.add(i)
            }
        }
        return list
    }

    fun singleSelectedChange(selected: Boolean) {
        tSound.playBetBallClickSound()
        if (selected)
            notifyMultiSelectedPositionWithPayLoads(getSingleBallSelectedPositions())
        else
            notifyUnSelectedAllWithPayLoads()
    }
    //endregion

    //region 选中双
    private fun getDoubleBallSelectedPositions(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in 0 until itemCount) {
            if (lotLotDuplexData.isStartZero) {
                if (i % 2 == 0)
                    list.add(i)
            } else {
                if (i % 2 != 0)
                    list.add(i)
            }
        }
        return list
    }

    fun doubleSelectedChange(selected: Boolean) {
        tSound.playBetBallClickSound()
        if (selected)
            notifyMultiSelectedPositionWithPayLoads(getDoubleBallSelectedPositions())
        else
            notifyUnSelectedAllWithPayLoads()
    }
    //endregion
    //endregion

    //region 冷热遗漏
    private var mHotLeaveVisible: Boolean? = null
    fun hotLeaveVisible(visible: Boolean?) {
        mHotLeaveVisible = visible
        notifyItemRangeChanged(0, itemCount, PAY_LOADS_HOT_LEAVE_VISIBLE)
    }
    //endregion

    //region 当前组选中号码
    fun getSelectedNums(): List<String> {
        var selectedNums = ArrayList<String>()
        val selectedPositions = getSelectedPositions()
        if (!selectedPositions.isNullOrEmpty()) {
            for (selectedPosition in selectedPositions) {
                val item = getItem(selectedPosition)
                if (!item.isSpace()) {
                    selectedNums.add(item)
                }
            }
        }
        return selectedNums
    }
    //endregion

    //region 渲染龙虎和：奖金
    fun renderLhhOddInfo(oddInfoMap: MutableMap<String, Double>?) {
        this.oddInfoMap = oddInfoMap
        notifyItemRangeChanged(0, itemCount, PAY_LOAD_ODD_INFO)
    }
    //endregion

    fun renderHot(hot: List<Int>?) {
        this.hot = hot
        notifyItemRangeChanged(0, itemCount, PAY_LOAD_HOT)
    }

    fun renderLeave(leave: List<Int>?) {
        this.leave = leave
        notifyItemRangeChanged(0, itemCount, PAY_LOAD_LEAVE)
    }
}