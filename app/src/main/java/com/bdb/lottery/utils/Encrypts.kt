package com.bdb.lottery.utils

import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


object Encrypts {
    //rsa加密
    fun rsaEncryptPublicKey(plainText: String, publicKey: String): String? {
        return encryptByPublicKey(plainText.toByteArray(), publicKey)?.run { base64Encode(this) }
    }


    fun base64Encode(bytes: ByteArray): String {
        return String(Base64().encode(bytes))
    }


    fun base64Decode(base64: String): ByteArray {
        return Base64().decode(base64.toByteArray())
    }

    fun encryptByPublicKey(data: ByteArray, key: String): ByteArray? {
        return try {
            val keyBase64 = base64Decode(key)
            val publicKey =
                KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(keyBase64))
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val size: Int = data.size
            val baos = ByteArrayOutputStream()
            var offset = 0
            while (size - offset > 0) {
                var cache: ByteArray? = null
                if (size - offset > 117) {
                    cache = cipher.doFinal(data, offset, 117)
                } else {
                    cache = cipher.doFinal(data, offset, size - offset)
                }
                baos.write(cache, 0, cache.size)
                offset += 117
            }
            val ret = baos.toByteArray()
            baos.close()
            ret
        } catch (e: Exception) {
            null
        }
    }
}