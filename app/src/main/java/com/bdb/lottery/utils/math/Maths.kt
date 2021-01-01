package com.bdb.lottery.utils.math

object Maths {
    fun combination(m: Int, n: Int): Int {
        var n = n
        return if (n != 0 && m != 0) {
            if (n > m) {
                0
            } else {
                if (n > m / 2) {
                    n = m - n
                }
                var a = 0.0
                var i: Int
                i = m
                while (i >= m - n + 1) {
                    a += Math.log(i.toDouble())
                    --i
                }
                i = n
                while (i >= 1) {
                    a -= Math.log(i.toDouble())
                    --i
                }
                a = Math.exp(a)
                Math.round(a).toInt()
            }
        } else {
            1
        }
    }
}