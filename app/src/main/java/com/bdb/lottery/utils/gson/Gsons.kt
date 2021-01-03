package com.bdb.lottery.utils.gson

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object Gsons {
    val gson = GsonBuilder().create()

    fun toJson(src: Any): String {
        return gson.toJson(src)
    }

    inline fun <reified T> fromJson(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    inline fun <reified T> fromJsonByTokeType(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }
}