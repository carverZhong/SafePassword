package com.github.carver.safepassword.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptUtil {

    fun encrypt(password: String, key: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = SecretKeySpec(key, "AES")

        // 生成 12 字节的随机 IV（推荐值）
        val iv = ByteArray(12).apply { SecureRandom().nextBytes(this) }
        val parameterSpec = GCMParameterSpec(128, iv) // GCM 认证标签长度 128 位

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)
        val ciphertext = cipher.doFinal(password.toByteArray(Charsets.UTF_8))

        // 合并 IV + 密文（便于存储/传输）
        val encryptedData = iv + ciphertext
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String, key: ByteArray): String {
        val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val iv = encryptedBytes.copyOfRange(0, 12) // 提取前 12 字节 IV
        val ciphertext = encryptedBytes.copyOfRange(12, encryptedBytes.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = SecretKeySpec(key, "AES")
        val parameterSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charsets.UTF_8)
    }
}
