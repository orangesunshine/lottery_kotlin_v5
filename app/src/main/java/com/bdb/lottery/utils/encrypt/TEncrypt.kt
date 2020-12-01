package com.bdb.lottery.utils.encrypt

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