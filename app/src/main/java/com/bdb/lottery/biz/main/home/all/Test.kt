package com.bdb.lottery.biz.main.home.all

class Test {
    fun main(args: Array<String>) {
        val methodName = "main"
        multiplyByTwo(5) { result: Int -> println("call method $methodName, Result is: $result") }
    }

    fun multiplyByTwo(num: Int,
                      lambda: (result: Int) -> Unit): Int {
        val result = num * 2
        lambda.invoke(result)
        return result
    }
}