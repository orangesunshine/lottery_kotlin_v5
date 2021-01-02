package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.CODE
import com.bdb.lottery.biz.account.AccountManager
import com.bdb.lottery.extension.code
import com.bdb.lottery.extension.msg
import com.bdb.lottery.utils.gson.Gsons
import com.bdb.lottery.utils.ui.activity.TActivityLifecycle
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BdbGsonConverterFactory @Inject constructor(
    private val gson: Gson,
    private val accountManager: AccountManager,
    private val tActivityLifecycle: TActivityLifecycle,
) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit,
    ): Converter<ResponseBody, *>? {
        return if (type is ParameterizedType && BaseResponse::class.java.isAssignableFrom(type.rawType as Class<*>)) {
            BdbGsonResponseBodyConverter(
                gson,
                gson.getAdapter(TypeToken.get(type)),
                tActivityLifecycle
            )
        } else GsonResponseBodyConverter(gson, gson.getAdapter(TypeToken.get(type)))
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit,
    ): Converter<*, RequestBody> {
        return BdbGsonRequestBodyConverter(gson,
            gson.getAdapter(TypeToken.get(type)),
            accountManager,
            tActivityLifecycle)
    }
}

//region repsonse
internal class BdbGsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>,
    private val tActivityLifecycle: TActivityLifecycle,
) :
    Converter<ResponseBody, T> {
    @Throws(Exception::class)
    override fun convert(value: ResponseBody): T {
        var bufferedReader: BufferedReader? = null
        val string = value.string()
        val jsonObject = JsonParser().parse(string).asJsonObject
        val code = jsonObject.get("code").asInt
        if (CODE.NET_SUCCESSFUL_CODE == code) {
            try {
                bufferedReader =
                    BufferedReader(InputStreamReader(ByteArrayInputStream(string.toByteArray())))
                val jsonReader =
                    gson.newJsonReader(bufferedReader)
                val result: T = adapter.read(jsonReader)
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw JsonIOException("JSON document was not fully consumed.")
                }
                return result
            } catch (e: Exception) {
                //ApiException返回自定义
                throw ApiException(BaseResponse(e.code, e.msg, string))
            } finally {
                bufferedReader?.close()
                value.close()
            }
        } else {
            val response = Gsons.fromJson<BaseResponse<*>>(string)
            tActivityLifecycle.topLogin(response)
            throw ApiException(response)
        }
    }
}

internal class GsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>,
) :
    Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val jsonReader = gson.newJsonReader(value.charStream())
        return try {
            val result = adapter.read(jsonReader)
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw JsonIOException("JSON document was not fully consumed.")
            }
            result
        } finally {
            value.close()
        }
    }
}
//endregion

//region request
internal class BdbGsonRequestBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>,
    private val accountManager: AccountManager,
    private val tActivityLifecycle: TActivityLifecycle,
) :
    Converter<T, RequestBody> {
    @Throws(Exception::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        writer.toString()
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }

    companion object {
        private val MEDIA_TYPE: MediaType = "application/json; charset=UTF-8".toMediaType()
        private val UTF_8 = Charset.forName("UTF-8")
    }
}
//endregion

class ApiException(val response: BaseResponse<*>) : RuntimeException()