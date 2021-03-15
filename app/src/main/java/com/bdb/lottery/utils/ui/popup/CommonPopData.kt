package com.bdb.lottery.utils.ui.popup

data class CommonPopData(
    var text: String? = null,
    var click: ((String?) -> Unit)? = null,
    var visible: Boolean = true,
    var imgRes: Int? = null,
)
