package com.github.carver.safepassword.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.carver.safepassword.data.kv.MainPasswordManager
import com.github.carver.safepassword.util.Md5Util
import com.github.carver.safepassword.util.ToastUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PasswordKeyboardViewModel : ViewModel() {
    private var keyboardState = if (MainPasswordManager.getMainPassword().isNullOrEmpty()) {
        STATE_FIRST_INPUT
    } else if (MainPasswordManager.isExpired()) {
        STATE_CHECK_INPUT
    } else {
        STATE_VALID_PASSWORD
    }

    private val _isShowDialog = mutableStateOf(
        keyboardState != STATE_VALID_PASSWORD
    )
    val isShowDialog: State<Boolean> get() = _isShowDialog

    private val _mainPassword = MutableStateFlow(List(PASSWORD_LENGTH) {""})
    val mainPassword: StateFlow<List<String>> = _mainPassword.asStateFlow()

    private var currentMainPassword = MainPasswordManager.getMainPassword()

    fun inputMainPassword(value: String) {
        val lastEmptyIndex = _mainPassword.value.indexOfFirst { it.isEmpty() }
        val newPassword = _mainPassword.value.toMutableList()
        newPassword[lastEmptyIndex] = value
        _mainPassword.value = newPassword
        if (lastEmptyIndex + 1 == PASSWORD_LENGTH) {
            onMainPasswordInputComplete(_mainPassword.value.joinToString(""))
            _mainPassword.value = List(PASSWORD_LENGTH) {""}
        }
    }

    fun deleteMainPassword() {
        val lastEmptyIndex = _mainPassword.value.indexOfFirst { it.isEmpty() }
        if (lastEmptyIndex == 0) {
            return
        }
        val newPassword = _mainPassword.value.toMutableList()
        newPassword[lastEmptyIndex - 1] = ""
        _mainPassword.value = newPassword
    }

    fun resetMainPassword() {
        if (keyboardState == STATE_SECOND_INPUT) {
            keyboardState = STATE_FIRST_INPUT
            currentMainPassword = null
        }
        _mainPassword.value = List(PASSWORD_LENGTH) {""}
        ToastUtil.show("已重置，请重新输入")
    }

    private fun onMainPasswordInputComplete(inputPassword: String) {
        if (keyboardState == STATE_CHECK_INPUT) {
            if (Md5Util.convert(inputPassword) == currentMainPassword) {
                _isShowDialog.value = false
                MainPasswordManager.savePasswordCheckTime()
            } else {
                ToastUtil.show("主密码输入错误，请重试", true)
            }
        } else if (keyboardState == STATE_FIRST_INPUT) {
            currentMainPassword = inputPassword
            keyboardState = STATE_SECOND_INPUT
            ToastUtil.show("请再次输入主密码", true)
        } else if (keyboardState == STATE_SECOND_INPUT) {
            if (currentMainPassword == inputPassword) {
                MainPasswordManager.saveMainPassword(Md5Util.convert(inputPassword))
                MainPasswordManager.savePasswordCheckTime()
                _isShowDialog.value = false
                ToastUtil.show("主密码设置成功，请妥善保管")
            } else {
                ToastUtil.show("两次主密码输入不一致，请再次输入或重置")
            }
        }
    }

    companion object {
        private const val PASSWORD_LENGTH = 8

        const val STATE_VALID_PASSWORD = 0
        const val STATE_FIRST_INPUT = 1
        const val STATE_SECOND_INPUT = 2
        const val STATE_CHECK_INPUT = 3
    }
}