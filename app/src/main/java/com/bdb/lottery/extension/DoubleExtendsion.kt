package com.bdb.lottery.extension

import java.math.RoundingMode
import java.text.DecimalFormat

val format = DecimalFormat("0.###").apply {
    roundingMode = RoundingMode.DOWN
}

val signFormat = DecimalFormat("0.###").apply {
    roundingMode = RoundingMode.DOWN
    positivePrefix = "+"
}

//double金额转string
fun Double.money(sign: Boolean = false): String {
    return (if (sign) signFormat else format).format(this)
}

fun Double.format(): String {
    return format.format(this)
}