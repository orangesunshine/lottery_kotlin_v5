package com.bdb.lottery.utils.encrypt

import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TEncrypt @Inject constructor() {
    //rsa加密
    fun rsaEncryptPublicKey(plainText: String, publicKey: String): String? {
        return Encrypts.encryptByPublicKey(plainText.toByteArray(), publicKey)
            ?.run { Encrypts.base64Encode(this) }
    }
}