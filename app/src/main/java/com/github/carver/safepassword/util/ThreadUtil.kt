package com.github.carver.safepassword.util

import android.os.Handler
import android.os.Looper

object ThreadUtil {

    val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    fun postMainThreadIfNeed(task: Runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            task.run()
        } else {
            mainHandler.post(task)
        }
    }
}