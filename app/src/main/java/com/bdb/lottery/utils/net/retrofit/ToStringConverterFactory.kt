package com.bdb.lottery.utils.net.retrofit

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ToStringConverterFactory : Converter.Factory() {
    val MEDIA_TYPE: MediaType? = "text/plain".toMediaTypeOrNull();
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {

        return if (type::class.java.equals(String::class.java)) {
            object : Converter<ResponseBody, String> {
                override fun convert(value: ResponseBody): String? {
                    return value.string()
                }
            }
        } else null
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return if (type::class.java.equals(String::class.java)) {
            object : Converter<String, RequestBody> {
                override fun convert(value: String): RequestBody? {
                    val toRequestBody = value.toRequestBody(MEDIA_TYPE)
                    return toRequestBody
                }
            }
        } else null
    }

    companion object {
        fun create(): ToStringConverterFactory {
            return ToStringConverterFactory()
        }
    }
}