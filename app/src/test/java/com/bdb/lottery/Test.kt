package com.bdb.lottery

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class Name(var name: String) {
    override fun toString(): String {
        return "Name(name='$name')"
    }
}

class Wrapper<T>(var data: T)

private inline fun <reified T> cast(wrapper: Wrapper<T>) {
    val json = "[{\"name\":\"younger1\"},{\"name\":\"younger2\"}, {\"name\":\"younger3\"}]"
    val fromJson: MutableList<Name> =
        GsonBuilder().create().fromJson<MutableList<Name>>(json, object : TypeToken<T>() {}.type)
    println(fromJson.map {
        it.name = "younger"
        it
    }.toMutableList().toString())
}

fun main() {
    val list = mutableListOf(Name("haha"))
    cast(Wrapper(list))
}