package com.github.carver.safepassword.viewmodel

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
    private val _passwordMap = MutableStateFlow<Map<String, List<PasswordEntity>>>(emptyMap())
    val passwordMap: StateFlow<Map<String, List<PasswordEntity>>> = _passwordMap

    init {
        loadPasswords()
    }

    fun loadPasswords() {
        viewModelScope.launch {
            val dbList = withContext(Dispatchers.IO) {
                PasswordDatabase.getDatabase().getPasswordDao().queryAll()
            }
            _passwordMap.value = dbList.groupBy { it.category }
        }
    }

}