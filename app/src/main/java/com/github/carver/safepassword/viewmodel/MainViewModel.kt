package com.github.carver.safepassword.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.carver.safepassword.data.source.local.PasswordDatabase
import com.github.carver.safepassword.data.source.local.PasswordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    private val _passwordList = MutableStateFlow<List<PasswordEntity>>(emptyList())
    val passwordList: StateFlow<List<PasswordEntity>> = _passwordList
    private val _passwordMap = MutableStateFlow<Map<String, List<PasswordEntity>>>(emptyMap())
    val passwordMap: StateFlow<Map<String, List<PasswordEntity>>> = _passwordMap
    private var _isShowCategory = mutableStateOf(false)
    val isShowCategory: State<Boolean> = _isShowCategory

    init {
        loadPasswords()
    }

    fun loadPasswords() {
        viewModelScope.launch {
            val dbList = withContext(Dispatchers.IO) {
                PasswordDatabase.getDatabase().getPasswordDao().queryAll()
            }
            _passwordList.value = dbList
            _passwordMap.value = dbList.groupBy { it.category }
        }
    }

    fun switchView() {
        _isShowCategory.value = !_isShowCategory.value
    }

}