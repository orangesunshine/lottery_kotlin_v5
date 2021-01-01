package com.bdb.lottery.utils.convert

import android.annotation.SuppressLint
import com.bdb.lottery.biz.lot.jd.single.AmountUnit
import com.bdb.lottery.const.MEM
import com.bdb.lottery.extension.isSpace
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

object Converts {
    private const val BUFFER_SIZE = 8192
    private val HEX_DIGITS_UPPER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val HEX_DIGITS_LOWER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    fun byte2FitMemorySize(byteSize: Long): String {
        return byte2FitMemorySize(byteSize, 3)
    }


    @SuppressLint("DefaultLocale")
    fun byte2FitMemorySize(byteSize: Long, precision: Int): String {
        require(precision >= 0) { "precision shouldn't be less than zero!" }
        return if (byteSize < 0) {
            throw IllegalArgumentException("byteSize shouldn't be less than zero!")
        } else if (byteSize < MEM.KB) {
            String.format("%." + precision + "fB", byteSize.toDouble())
        } else if (byteSize < MEM.MB) {
            java.lang.String.format(
                "%." + precision + "fKB",
                byteSize.toDouble() / MEM.KB
            )
        } else if (byteSize < MEM.GB) {
            java.lang.String.format(
                "%." + precision + "fMB",
                byteSize.toDouble() / MEM.MB
            )
        } else {
            java.lang.String.format(
                "%." + precision + "fGB",
                byteSize.toDouble() / MEM.GB
            )
        }
    }

    fun bytes2HexString(bytes: ByteArray?): String {
        return bytes2HexString(bytes, true)
    }

    fun bytes2HexString(bytes: ByteArray?, isUpperCase: Boolean): String {
        if (bytes == null) return ""
        val hexDigits: CharArray =
            if (isUpperCase) HEX_DIGITS_UPPER else HEX_DIGITS_LOWER
        val len = bytes.size
        if (len <= 0) return ""
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = hexDigits[bytes[i].toInt() shr 4 and 0x0f]
            ret[j++] = hexDigits[(bytes[i] and 0x0f).toInt()]
            i++
        }
        return String(ret)
    }

    /**
     * Input stream to bytes.
     */
    fun inputStream2Bytes(ins: InputStream?): ByteArray? {
        return if (ins == null) null else input2OutputStream(
            ins
        )?.toByteArray()
    }

    /**
     * Bytes to input stream.
     */
    fun bytes2InputStream(bytes: ByteArray?): InputStream? {
        return if (bytes == null || bytes.size <= 0) null else ByteArrayInputStream(bytes)
    }

    /**
     * Input stream to output stream.
     */
    fun input2OutputStream(ins: InputStream?): ByteArrayOutputStream? {
        return if (ins == null) null else try {
            val os = ByteArrayOutputStream()
            val b = ByteArray(BUFFER_SIZE)
            var len: Int
            while (ins.read(b, 0, BUFFER_SIZE).also {
                    len = it
                } != -1) {
                os.write(b, 0, len)
            }
            os
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                ins.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun inputStream2Lines(ins: InputStream?): List<String?>? {
        return inputStream2Lines(ins, "")
    }

    fun inputStream2Lines(
        ins: InputStream?,
        charsetName: String,
    ): List<String?>? {
        var reader: BufferedReader? = null
        return try {
            val list: MutableList<String?> = ArrayList()
            reader = BufferedReader(InputStreamReader(ins, getSafeCharset(charsetName)))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                list.add(line)
            }
            list
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getSafeCharset(charsetName: String): String? {
        var cn = charsetName
        if (charsetName.isSpace() || !Charset.isSupported(charsetName)) {
            cn = "UTF-8"
        }
        return cn
    }

    //下注单位转换
    fun unit2String(unit: Int): String {
        return when (unit) {
            1 -> "元"
            2 -> "角"
            3 -> "分"
            4 -> "厘"
            else -> ""
        }
    }

    fun unit2Params(unit: Int): Int {
        return when (unit) {
            1 -> 1
            2 -> 10
            3 -> 100
            4 -> 1000
            else -> 1
        }
    }

    //下注单位转换
    fun unit2Enum(unit: Int): AmountUnit {
        return when (unit) {
            1 -> AmountUnit.YUAN
            2 -> AmountUnit.JIAO
            3 -> AmountUnit.FEN
            4 -> AmountUnit.LI
            else -> AmountUnit.YUAN
        }
    }
}