package com.bdb.lottery.utils.net.retrofit

import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.const.ICode
import com.bdb.lottery.extension.code
import com.bdb.lottery.extension.msg
import com.google.gson.*
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
import timber.log.Timber
import java.io.*
import java.lang.reflect.Type
import java.nio.charset.Charset

class BdbGsonConverterFactory private constructor(private val gson: Gson) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val adapter: TypeAdapter<*> = gson.getAdapter(TypeToken.get(type))
        return BdbGsonResponseBodyConverter(gson, adapter)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter: TypeAdapter<*> = gson.getAdapter(TypeToken.get(type))
        return BdbGsonRequestBodyConverter(gson, adapter)
    }

    companion object {
        /**
         * Create an instance using `gson` for conversion. Encoding to JSON and decoding from JSON
         * (when no charset is specified by a header) will use UTF-8.
         */
        /**
         * Create an instance using a default [Gson] instance for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        @JvmOverloads  // Guarding public API nullability.
        fun create(gson: Gson? = Gson()): BdbGsonConverterFactory {
            if (gson == null) throw NullPointerException("gson == null")
            return BdbGsonConverterFactory(gson)
        }
    }
}

internal class BdbGsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) :
    Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        var string: String? = null
        var bufferedReader: BufferedReader? = null
        return try {
            string = value.string()
            val jsonObject = JsonParser().parse(string).asJsonObject
            val code = jsonObject.get("code").asInt
            if (ICode.CODE_SUCCESSFUL == code) {
                bufferedReader =
                    BufferedReader(InputStreamReader(ByteArrayInputStream(string.toByteArray())))
                val jsonReader =
                    gson.newJsonReader(bufferedReader)
                val result = adapter.read(jsonReader)
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw JsonIOException("JSON document was not fully consumed.")
                }
                return result
            } else {
                throw ApiException(
                    GsonBuilder().create().fromJson(string, BaseResponse::class.java)
                )
            }
        } catch (e: Exception) {
            Timber.d("BdbGsonRequestBodyConverter: ${e}")
            //ApiException返回自定义
            throw if (e !is ApiException) {
                ApiException(BaseResponse(e.code, e.msg, string))
            } else {
                e
            }
        } finally {
            bufferedReader?.close()
            value.close()
        }
    }
}

internal class BdbGsonRequestBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) :
    Converter<T, RequestBody> {
    @Throws(IOException::class)
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

class ApiException(val response: BaseResponse<*>) : RuntimeException()

//错误结果解析
//inline fun <reified T> ApiException.parseErrorData(): T? {
//    return response?.let {
//        it.data?.let {
//            return GsonBuilder().create().fromJson(it, T::class.java)
//        }
//    }
//}
//
////登录验证码
//inline fun ApiException.needValidate(): VerifycodeData? {
//    return parseErrorData<VerifycodeData>()
//}