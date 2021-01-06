package com.bdb.lottery.biz.lot.jd.duplex.adapter

import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class LotDuplexSubAdapter constructor(
    list: List<String>?
) : BaseSelectedQuickAdapter<String, BaseViewHolder>(
    R.layout.lot_duplex_item,
    list?.toMutableList()
) {
    override fun convert(holder: BaseViewHolder, item: String) {

    }

    //region 大小单双
    //选中大
    fun selectedBig() {

    }

    //选中小
    fun selectedSmall() {

    }

    //选中单
    fun selectedSingle() {

    }

    //选中双
    fun selectedDouble() {

    }
    //endregion
}