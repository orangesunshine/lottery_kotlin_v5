package com.bdb.lottery.event

import org.greenrobot.eventbus.EventBus

/**
 * eventbus发送事件管理
 */
object TEventManager {

    //region 发送全局余额事件
    fun postBalanceEvent() {
        EventBus.getDefault().post(BalanceEvent())
    }
    //endregion
}