package com.bdb.lottery.utils.gson

import com.google.gson.GsonBuilder
import java.lang.reflect.Type

object Gsons {
    val gson = GsonBuilder().create()

    fun toJson(src: Any): String? {
        return gson.toJson(src)
    }

    fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }
}