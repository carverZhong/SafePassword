package com.github.carver.safepassword.data.kv
import com.github.carver.safepassword.util.Md5Util

object MainPasswordManager {

    private const val KEY_MAIN_PASSWORD = "user_main_password"
    private const val KEY_EXPIRATION_TIME = "user_password_check"
    private const val DEFAULT_EXPIRATION_TIME = 3 * 24 * 60 * 60 * 1000

    fun getMainPassword(): String? {
        return KVStorage.getString(KEY_MAIN_PASSWORD, null)
    }

    fun saveMainPassword(password: String) {
        KVStorage.save(KEY_MAIN_PASSWORD, password)
    }

    fun isExpired(): Boolean {
        val checkTime = KVStorage.getLong(KEY_EXPIRATION_TIME)
        return System.currentTimeMillis() - checkTime > DEFAULT_EXPIRATION_TIME
    }

    fun savePasswordCheckTime() {
        KVStorage.save(KEY_EXPIRATION_TIME, System.currentTimeMillis())
    }
}