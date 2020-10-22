package com.bdb.lottery

import android.text.TextUtils
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun String_equals_isCorrect() {
        val list = arrayOf("", "", "aaa")
        val filter = list.filter { !isEmpty(it) }
        println("list: ${list.asList()}, filer: ${filter}")
    }

    fun isEmpty(str: String):Boolean {
        return str == null || str.length == 0
    }
}