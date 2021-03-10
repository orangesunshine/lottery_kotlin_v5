package com.bdb.lottery.utils.ui.popup

data class CommonPopData(
    val text: String? = null,
    val click: ((String?) -> Unit)?,
    val imgRes: Int? = null
)
