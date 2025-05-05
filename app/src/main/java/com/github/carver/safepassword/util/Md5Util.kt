package com.github.carver.safepassword.util

import java.security.MessageDigest

object Md5Util {

    fun convert(data: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(data.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

}