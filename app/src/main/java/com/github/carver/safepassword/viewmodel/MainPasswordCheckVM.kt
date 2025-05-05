package com.github.carver.safepassword.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.carver.safepassword.data.kv.MainPasswordManager
import com.github.carver.safepassword.util.Md5Util

class MainPasswordCheckVM : ViewModel() {
    private val savedPassword = MainPasswordManager.getMainPassword()
    private var checkPassword = savedPassword
    private val isFirstUse = savedPassword == null
    private var isInDoubleCheck = false
    var mainTipText = mutableStateOf(if (isFirstUse) "请设置主密码" else "请输入主密码")

    fun passwordComplete(password: String, onPasswordInput: (Boolean, String) -> Unit) {
        val passwordMd5 = Md5Util.convert(password)
        if (isFirstUse && checkPassword.isNullOrEmpty()) {
            checkPassword = passwordMd5
            isInDoubleCheck = true
            mainTipText.value = "请再次输入主密码"
        } else if (isInDoubleCheck && checkPassword?.isNotEmpty() == true) {
            if (passwordMd5 == checkPassword) {
                MainPasswordManager.saveMainPassword(passwordMd5)
                onPasswordInput(true, "")
            } else {
                onPasswordInput(false, "两次密码不一致，请重新输入")
                checkPassword = null
            }
            isInDoubleCheck = false
        } else if (passwordMd5 == checkPassword) {
            onPasswordInput(true, "")
        } else {
            onPasswordInput(false, "主密码输入错误，请重新输入")
        }
    }

}