package com.bdb.lottery.utils.ui.popup

data class CommonPopData(
    private val imgRes: Int? = null,
    private val text: String? = null,
    private val click: ((String) -> Unit)?
)
