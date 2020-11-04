package com.bdb.lottery

import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.money
import com.google.gson.GsonBuilder
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.regex.Matcher
import java.util.regex.Pattern

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

    fun isEmpty(str: String): Boolean {
        return str == null || str.length == 0
    }

    @Test
    fun regexMatcher() {
        // 要验证的字符串
        val str = "s29360705@xsoftlab.net"
        // 邮箱验证规则
        val regEx = "\"^[0-9]+(.[0-9]{2})?\$\""
//        val regEx = "[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}"
        // 编译正则表达式
        val pattern: Pattern = Pattern.compile(regEx)
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        val matcher: Matcher = pattern.matcher(str)
        // 字符串是否与正则表达式相匹配
        val rs: Boolean = matcher.matches()
        Assert.assertTrue(rs)
    }

    @Test
    fun hasMomain() {
        val url = "http://baidu.com"
        val regex =
            "((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)?"
        assertTrue(Pattern.matches(regex, url))
    }

    class A {
        var age: Int? = null
    }

    class B {
        var a: A? = null
    }

    class C {
        var b: B? = null
    }

    @Test
    fun assertNUll() {
        val c = C()
        val ret = c?.b ?: null
        println("ret: ${ret}")
    }

    @Test
    fun assertLet() {
        var a = ""
        a?.let {
            println("let")
            return
        }
        println("testLet")
    }

    @Test
    fun assertIsSpace() {
        val a: String? = null
        val b: String? = ""
        val c: String? = " "
        val d: String? = "  "
        println("a.isSpace()" + a.isSpace())
        println("a.isSpace()" + b.isSpace())
        println("a.isSpace()" + c.isSpace())
        println("a.isSpace()" + d.isSpace())
    }

    @Test
    fun assertMoney() {
        val money2: Double = 0.010
        val money3: Double = 346211313.001
        println(money2.money())
        println(money3.money())
    }

    @Test
    fun assertGson(){
        val a:Int? = null
        val gson = GsonBuilder().create().toJson(a)
        println(gson)
    }
}