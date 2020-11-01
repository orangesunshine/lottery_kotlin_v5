package com.bdb.lottery.extension

import java.math.RoundingMode
import java.text.DecimalFormat

val format = DecimalFormat("##0").apply {
    roundingMode = RoundingMode.DOWN
    maximumFractionDigits = 3
    isGroupingUsed = false
}

val signFormat = DecimalFormat("##0").apply {
    roundingMode = RoundingMode.DOWN
    maximumFractionDigits = 3
    isGroupingUsed = false
    positivePrefix = "+"
}

//double金额转string
fun Double.money(sign: Boolean = false): String {
    return (if (sign) signFormat else format).format(this)
}