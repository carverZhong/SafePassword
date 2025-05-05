package com.github.carver.safepassword.util

import android.widget.Toast
import com.github.carver.safepassword.SafePasswordApplication

object ToastUtil {

    fun show(message: String, isLongDuration: Boolean = false) {
        ThreadUtil.postMainThreadIfNeed {
            Toast.makeText(
                SafePasswordApplication.getInstance(),
                message,
                if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
        }
    }
}