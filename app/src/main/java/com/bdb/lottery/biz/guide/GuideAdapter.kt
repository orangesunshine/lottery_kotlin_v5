package com.bdb.lottery.biz.guide

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class GuideAdapter(val itemLayoutId: Int, val datas: MutableList<Int>) :
    BaseQuickAdapter<Int, BaseViewHolder>(itemLayoutId, datas) {

    override fun convert(holder: BaseViewHolder, item: Int) {

    }

}