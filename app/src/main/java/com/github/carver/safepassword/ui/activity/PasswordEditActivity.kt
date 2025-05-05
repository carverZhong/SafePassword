package com.github.carver.safepassword.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.github.carver.safepassword.data.source.local.PasswordEntity
import com.github.carver.safepassword.ui.theme.SafePasswordTheme
import com.github.carver.safepassword.viewmodel.EditViewModel
import kotlinx.coroutines.launch

class PasswordEditActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 有值说明是修改项，无值说明是添加项！
        val password: PasswordEntity? = intent.getParcelableExtra(KEY_PASSWORD)
        val viewModel = EditViewModel(password)
        setContent {
            SafePasswordTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = if (password == null) "添加密码" else "编辑密码") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.surface,
                            ),
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back", // 无障碍描述
                                        tint = MaterialTheme.colorScheme.onPrimary // 图标颜色
                                    )
                                }
                            },
                            actions = {
                                Text(
                                    modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp).clickable {
                                        if (password == null) {
                                            viewModel.addPassword()
                                        } else {
                                            viewModel.updatePassword(password)
                                        }
                                    },
                                    text = if (password == null) "添加" else "更新",
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    EditContent(innerPadding, viewModel)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.canFinishActivity.collect { finish ->
                if (finish) {
                    finish()
                }
            }
        }
    }

    companion object {
        private const val KEY_PASSWORD = "bundle_key_password"

        fun start(context: Context, password: PasswordEntity?) {
            context.startActivity(Intent(context, PasswordEditActivity::class.java).apply {
                if (password != null) {
                    putExtra(KEY_PASSWORD, password)
                }
            })
        }
    }
}

@Composable
fun EditContent(innerPadding: PaddingValues, viewModel: EditViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp, innerPadding.calculateTopPadding() + 6.dp, 16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedTextField(
            value = viewModel.category.value,
            onValueChange = { viewModel.category.value = it },
            label = { Text("请输入应用/网站名") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default,
            singleLine = true
        )
        OutlinedTextField(
            value = viewModel.account.value,
            onValueChange = { viewModel.account.value = it },
            label = { Text("请输入账号名") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default,
            singleLine = true
        )
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("请输入密码") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (viewModel.isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (viewModel.isPasswordVisible.value) {
                    Icons.Default.Visibility // 显示密码图标
                } else {
                    Icons.Default.VisibilityOff // 隐藏密码图标
                }

                // 密码可见性切换按钮
                IconButton(onClick = { viewModel.isPasswordVisible.value = !viewModel.isPasswordVisible.value }) {
                    Icon(imageVector = image, contentDescription = if (viewModel.isPasswordVisible.value) "Hide password" else "Show password")
                }
            },
            singleLine = true
        )
        OutlinedTextField(
            value = viewModel.remark.value,
            onValueChange = { viewModel.remark.value = it },
            label = { Text("请输入备注") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default,
            singleLine = false // 允许多行输入
        )

    }
}