package com.bdb.lottery.biz.lot

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bdb.lottery.R
import com.bdb.lottery.const.GAME
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.module.application.AppEntries
import com.bdb.lottery.utils.lot.Lots
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import dagger.hilt.android.EntryPointAccessors

class BallAdapter(
    private val gameType: Int,
    private val parentPlayId: Int,
    private val playId: Int,
    private val ballSize: Int,//球大小
    data: MutableList<HistoryData.HistoryItem>? = null,
) : BaseQuickAdapter<HistoryData.HistoryItem, BaseViewHolder>(R.layout.lot_history_item, data) {
    private val tGame by lazy {
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTGame()
    }

    //玩法指定号码下标
    private val brightLists by lazy {
        tGame.brightIndexs("", gameType)
    }

    //获取期号
    private fun getIssue(item: HistoryData.HistoryItem): String {
        return "第" + item.issueno + "期"
    }

    //显示分割线
    private fun divideVisible(item: HistoryData.HistoryItem): Boolean {
        return true
    }

    //形态
    private fun labelText(item: HistoryData.HistoryItem): String {
        return Lots.getLabel(gameType, parentPlayId, playId, item.nums)
    }

    //球
    fun ball(position: Int, num: String?): TextView {
        val textView = TextView(context)
        textView.layoutParams = ViewGroup.MarginLayoutParams(ballSize, ballSize)
        textView.gravity = Gravity.CENTER
        textView.text = num
        textView.setBackgroundResource(R.drawable.lot_open_nums_white_circle_shape)
        textView.alpha = if (brightLists.contains(position + 1)) 1f else 0.4f
        return textView
    }

    override fun convert(holder: BaseViewHolder, item: HistoryData.HistoryItem) {
        if (GAME.TYPE_GAME_K3 == gameType) holder.setBackgroundResource(
            R.id.lot_history_item_horizontal_divide_view,
            R.color.color_skin_k3_line
        )//期号
        holder.setText(R.id.lot_history_item_issue_tv, getIssue(item))//期号
        holder.setVisible(R.id.lot_history_item_divide_view, divideVisible(item))
        holder.setText(R.id.lot_history_item_label_tv, labelText(item))
        val nums = item.nums
        nums?.let {
            val split = nums.split(" ")
            holder.getView<TagFlowLayout>(R.id.lot_history_item_nums_fl).adapter =
                object : TagAdapter<String>(split) {
                    override fun getView(parent: FlowLayout?, position: Int, num: String?): View {
                        return ball(position, num)
                    }
                }
        }
    }
}