package com.bdb.lottery

import com.bdb.lottery.extension.notNullEquals
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
    fun String_equals_isCorrect(){
        val str1:String? = null
        val str2:String? = null
        val ret = str1.notNullEquals(str2)
        println("ret: ${ret}")
    }

}