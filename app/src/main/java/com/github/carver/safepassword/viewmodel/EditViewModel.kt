package com.github.carver.safepassword.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.carver.safepassword.data.kv.MainPasswordManager
import com.github.carver.safepassword.data.source.local.PasswordDatabase
import com.github.carver.safepassword.data.source.local.PasswordEntity
import com.github.carver.safepassword.util.EncryptUtil
import com.github.carver.safepassword.util.ToastUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(passwordEntity: PasswordEntity?) : ViewModel() {
    private val _canFinishActivity = MutableStateFlow(false)
    val canFinishActivity : StateFlow<Boolean> = _canFinishActivity
    val category = mutableStateOf(passwordEntity?.category ?: "")
    val account = mutableStateOf(passwordEntity?.account ?: "")
    val password = mutableStateOf(passwordEntity?.password ?: "")
    val remark = mutableStateOf(passwordEntity?.remark ?: "")
    val isPasswordVisible = mutableStateOf(false)

    fun addPassword() {
        if (!checkInputAndToast()) {
            return
        }
        val passwordEntity = PasswordEntity(
            category = category.value,
            time = System.currentTimeMillis(),
            account = account.value,
            password = password.value,
            remark = remark.value
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val passwordForInsert = PasswordEntity(
                    category = passwordEntity.category,
                    time = passwordEntity.time,
                    account = passwordEntity.account,
                    password = EncryptUtil.encrypt(passwordEntity.password, MainPasswordManager.getMainPassword()!!.toByteArray()),
                    remark = passwordEntity.remark
                )
                PasswordDatabase.getDatabase().getPasswordDao().insert(passwordForInsert)
            }
            ToastUtil.show("已添加新密码")
            _canFinishActivity.value = true
        }
    }

    fun updatePassword(oldPassword: PasswordEntity) {
        if (!checkInputAndToast()) {
            return
        }
        val passwordEntity = PasswordEntity(
            id = oldPassword.id,
            category = category.value,
            time = System.currentTimeMillis(),
            account = account.value,
            password = password.value,
            remark = remark.value
        )
        if (oldPassword.isTheSameContent(passwordEntity)) {
            ToastUtil.show("无任何修改")
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val passwordForUpdate = PasswordEntity(
                    id = passwordEntity.id,
                    category = passwordEntity.category,
                    time = passwordEntity.time,
                    account = passwordEntity.account,
                    password = EncryptUtil.encrypt(passwordEntity.password, MainPasswordManager.getMainPassword()!!.toByteArray()),
                    remark = passwordEntity.remark
                )
                PasswordDatabase.getDatabase().getPasswordDao().update(passwordForUpdate)
            }
            ToastUtil.show("密码更新成功")
            _canFinishActivity.value = true
        }
    }

    private fun checkInputAndToast(): Boolean {
        if (category.value.isEmpty()) {
            ToastUtil.show("应用/网站名不能为空")
            return false
        }
        if (account.value.isEmpty()) {
            ToastUtil.show("账号名不能为空")
            return false
        }
        if (password.value.isEmpty()) {
            ToastUtil.show("密码不能为空")
            return false
        }
        return true
    }
}