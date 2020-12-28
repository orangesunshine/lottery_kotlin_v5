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
    fun assertGson() {
        val a: Int? = null
        val gson = GsonBuilder().create().toJson(a)
        println(gson)
    }

    class Name(var name: String) {
        override fun toString(): String {
            return "Name(name='$name')"
        }
    }

    class WrapperData(var age: MutableList<Name>)

    class Wrapper<T>(var data: T)

    private inline fun <reified T> cast(wrapper: Wrapper<T>) {
        val json = "[{\"name\":\"younger1\"},{\"name\":\"younger2\"}, {\"name\":\"younger3\"}]"
        println(T::class.java.name)
        val fromJson: MutableList<Name> =
            GsonBuilder().create().fromJson<MutableList<Name>>(json, T::class.java)
        val iterator = fromJson.iterator()
        println("it")
    }

    class Ref() {
        lateinit var ref: MutableList<WrapperData>
    }

    @Test
    fun reifiedFunTest() {
        var ref = Ref()
        val list = mutableListOf(Name("haha"))
        cast(Wrapper(list))
    }

    @Test
    fun iteratorModifyTest() {
        val list = mutableListOf<String>("a", "b", "c", "d", "e", "f")
        val listIterator =
            list.listIterator().apply {
                while (hasNext()) {
                    set("younger")
                }
            }
    }

    @Test
    fun assertReduce(){
        val list= listOf(1, 2, 3)
        val result = list.reduce { s, t ->
            print("s=$s ")
            print("t=$t")
            println()
            s + t
        }
        println(result)
    }

    @Test
    fun test(){
        val phonenum = "13177668655"
        val chpier = phonenum.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")
        println(chpier)
    }

    @Test
    fun match(){
        val compile = Pattern.compile("(http|https)://([\\w.]+/?)\\w*")
        val matcher = compile.matcher("fjdsfjlk你好吗http://www.baidu.com/你好啊")
        if (matcher.find()) {
            val group = matcher.group()
            val start = matcher.start()
            val end = matcher.end()
            println("group: ${group}, start: ${start}, end: ${end}")
        }
    }
}