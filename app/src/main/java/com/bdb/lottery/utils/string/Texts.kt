package com.bdb.lottery.utils.string

import java.util.*
import kotlin.math.min

object Texts {
    fun isBlank(charSequence: CharSequence?): Boolean {
        if (null == charSequence || charSequence.isEmpty()) return true
        for (element in charSequence) {
            if (!Character.isWhitespace(element)) return false
        }
        return true
    }

    fun toIntegerArray(str: String, regex: String): IntArray {
        val list = toIntList(str, regex)
        return list.toIntArray()
    }

    private fun toIntList(str: String, regex: String): List<Int> {
        val t = StringTokenizer(str, regex)
        val list: ArrayList<Int> = ArrayList()
        while (t.hasMoreElements()) {
            val s = t.nextToken()
            if (!isBlank(s)) {
                list.add(s.toInt())
            }
        }
        return list
    }

    fun partition(str: String, partition: Int): MutableList<String?> {
        val parts: MutableList<String?> = mutableListOf()
        val len = str.length
        var i = 0
        while (i < len) {
            parts.add(str.substring(i, min(len, i + partition)))
            i += partition
        }
        return parts
    }
}