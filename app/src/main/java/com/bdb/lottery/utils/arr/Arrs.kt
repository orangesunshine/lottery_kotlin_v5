package com.bdb.lottery.utils.arr

import java.util.*

object Arrs {
    fun duplicate(arr: Array<*>?): Boolean {
        if (null == arr) return false
        for (i in 0 until arr!!.size - 1) {
            for (j in i + 1 until arr.size) {
                if (arr[i] == arr[j]) {
                    return true
                }
            }
        }
        return false
    }

    fun intersect(one: Array<*>, two: Array<*>): Array<*> {
        val a: MutableSet<Any> = HashSet(listOf(*one))
        val b: Set<*> = HashSet(two.asList())
        a.retainAll(b)
        return a.toTypedArray()
    }

    fun join(array: Array<*>, str: String): String {
        val sb = StringBuffer()
        for (element in array) {
            sb.append(element).append(str)
        }
        sb.delete(sb.length - str.length, sb.length)
        return sb.toString()
    }
}