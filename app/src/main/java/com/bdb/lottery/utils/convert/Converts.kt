package com.bdb.lottery.utils.convert

import android.annotation.SuppressLint
import com.bdb.lottery.const.MEM
import kotlin.experimental.and

object Converts {
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
}