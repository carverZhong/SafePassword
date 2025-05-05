package com.github.carver.safepassword.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.carver.safepassword.data.kv.KVStorage
import com.github.carver.safepassword.data.kv.KVStorageKey
import com.github.carver.safepassword.ui.activity.SplashActivity
import com.github.carver.safepassword.util.ThreadUtil
import com.github.carver.safepassword.util.ToastUtil
import com.github.carver.safepassword.viewmodel.MainPasswordCheckVM
import java.lang.ref.WeakReference

class SplashActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashContent { isSuccess, errorMsg ->
                if (isSuccess) {
                    ThreadUtil.mainHandler.postDelayed(DelayStart(this@SplashActivity), 500)
                } else {
                    ToastUtil.show(errorMsg, false)
                }
            }
            DialogContent()
        }
    }

    private class DelayStart(activity: Activity): Runnable {
        private val actRef = WeakReference(activity)

        override fun run() {
            val activity = actRef.get() ?: return
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }

    }
}


@Composable
fun SplashContent(viewModel: MainPasswordCheckVM = viewModel(), onPasswordInput: (Boolean, String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        focusManager.moveFocus(FocusDirection.Up)
        keyboardController?.show()
    }

    var inputPassword by remember { mutableStateOf("") }
    val topPadding = LocalConfiguration.current.screenWidthDp.dp * 3 / 5
    val startEndPadding = 30.dp
    Column(modifier = Modifier.fillMaxSize().padding(startEndPadding, topPadding, startEndPadding, 0.dp)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = viewModel.mainTipText.value,
            color = Color.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.fillMaxWidth().height(70.dp))
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                if (index < inputPassword.length) Color(0xFFAAAAAA) else Color(0xFFDDDDDD),
                                shape = CircleShape
                            )
                    )
                }
            }


            // 输入框（隐藏实际输入）
            TextField(
                value = inputPassword,
                onValueChange = { newValue ->
                    val digitsOnly = newValue.filter { it.isDigit() }
                    if (digitsOnly.length <= 6) {
                        inputPassword = digitsOnly
                        if (digitsOnly.length == 6) {
                            viewModel.passwordComplete(inputPassword, onPasswordInput)
                            inputPassword = ""
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Transparent
                ),
                visualTransformation = PasswordVisualTransformation(), // 隐藏输入（可选）
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(0f) // 隐藏输入框（仅用于触发键盘
            )
        }
    }
}

@Composable
fun DialogContent() {
    var isShowDialog by remember { mutableStateOf(!KVStorage.getBool(KVStorageKey.KEY_HAS_USED_APP, false)) }
    if (isShowDialog) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false, // 禁止通过返回键关闭
                dismissOnClickOutside = false // 禁止通过点击外部关闭
            )
        ) {
            // 表单对话框内容
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "温馨提示",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        text = "主密码是进入应用的唯一凭证，设置后请妥善保管")
                    HorizontalDivider()
                    Button(
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // 背景色透明
                            contentColor = Color.Black // 文字颜色
                        ),
                        onClick = {
                            isShowDialog = false
                            KVStorage.save(KVStorageKey.KEY_HAS_USED_APP, true)
                        }
                    ) {
                        Text(
                            fontSize = 15.sp,
                            text = "我知道了"
                        )
                    }
                }
            }
        }
    }
}