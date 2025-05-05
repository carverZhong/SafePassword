package com.github.carver.safepassword.data.kv

import android.content.Context
import com.tencent.mmkv.MMKV
import timber.log.Timber

/**
 * 封装 MMKV 细节，用于存储 key-value 数据。
 */
object KVStorage {

    private lateinit var mmkvInstance: MMKV

    fun init(context: Context) {
        val rootDir = MMKV.initialize(context)
        mmkvInstance = MMKV.defaultMMKV()
        Timber.d("init: success, rootDir=$rootDir")
    }

    fun save(key: String, value: Boolean) {
        mmkvInstance.encode(key, value)
    }

    fun getBool(key: String, defaultValue: Boolean = false): Boolean {
        return mmkvInstance.decodeBool(key, defaultValue)
    }

    fun save(key: String, value: String) {
        mmkvInstance.encode(key, value)
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return mmkvInstance.decodeString(key, defaultValue)
    }

    fun save(key: String, value: Long) {
        mmkvInstance.encode(key, value)
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return mmkvInstance.getLong(key, defaultValue)
    }

}