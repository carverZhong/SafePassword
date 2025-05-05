package com.github.carver.safepassword

import android.app.Application
import android.content.Context
import android.util.Log
import com.github.carver.safepassword.data.kv.KVStorage
import com.tencent.mmkv.BuildConfig
import timber.log.Timber

class SafePasswordApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        initTimber()
        KVStorage.init(this)
    }


    companion object {
        private lateinit var instance: SafePasswordApplication

        fun getInstance(): SafePasswordApplication {
            return instance
        }

        private fun initTimber() {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            } else {
                Timber.plant(object : Timber.Tree() {
                    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                        var logPrinter: ((tag: String?, message: String?, t: Throwable?) -> Int)? = null
                        when (priority) {
                            Log.ERROR, Log.ASSERT -> logPrinter = Log::e
                            Log.WARN -> logPrinter = Log::w
                        }
                        logPrinter?.invoke(tag, message, t)
                    }

                })
            }
        }
    }
}