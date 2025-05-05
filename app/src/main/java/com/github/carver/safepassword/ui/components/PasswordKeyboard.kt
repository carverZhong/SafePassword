package com.github.carver.safepassword.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.carver.safepassword.viewmodel.PasswordKeyboardViewModel

private val KEYBOARD_CONTENT = listOf("1", "2", "3",
    "4", "5", "6",
    "7", "8", "9",
    "重置", "0", "X")
private const val INDEX_RESET = 9
private val INDEX_DELETE = KEYBOARD_CONTENT.size - 1

@Composable
fun ShowPasswordKeyboardDialog(viewModel: PasswordKeyboardViewModel) {
    AnimatedVisibility(visible = viewModel.isShowDialog.value,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        if (viewModel.isShowDialog.value) {
            Dialog(onDismissRequest = {} ) {
                PasswordKeyboardContent(viewModel)
            }
        }
    }
}

@Composable
fun PasswordKeyboardContent(viewModel: PasswordKeyboardViewModel) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val width = screenWidth * 0.9
    val height = width * 1.3
    Box(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 8.dp, top = 10.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "请输入主密码",
                fontSize = 20.sp,
                color = Color.Black,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(30.dp).fillMaxWidth())
            PasswordInput(viewModel)
            Spacer(modifier = Modifier.weight(1f).fillMaxWidth())
            PasswordKeyboard(KEYBOARD_CONTENT) { index, password ->
                when (index) {
                    INDEX_DELETE -> {
                        viewModel.deleteMainPassword()
                    }
                    INDEX_RESET -> {
                        viewModel.resetMainPassword()
                    }
                    else -> {
                        viewModel.inputMainPassword(password)
                    }
                }
            }
        }
    }
}


@Composable
fun PasswordInput(viewModel: PasswordKeyboardViewModel) {
    val padding = 30.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .padding(PaddingValues(start = padding, end = padding)),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(8) { index ->
            PasswordDot(index, viewModel)
        }
    }
}

@Composable
fun PasswordDot(index: Int, viewModel: PasswordKeyboardViewModel) {
    val password = viewModel.mainPassword.collectAsState()
    if (password.value[index].isEmpty()) {
        Box(modifier = Modifier.size(16.dp).border(2.dp, Color.LightGray, CircleShape))
    } else {
        Box(modifier = Modifier.size(16.dp).background(Color.Red, CircleShape))
    }
}

@Composable
fun PasswordKeyboard(dataItems: List<String>, onPasswordChange: (Int, String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(30.dp),
        content = {
            items(dataItems.size) { index ->
                TextButton (
                    onClick = { onPasswordChange.invoke(index, dataItems[index]) },
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                ) {
                    Text(dataItems[index],
                        fontSize = if (index == INDEX_RESET) 18.sp else 30.sp,
                        color = Color.Black
                    )
                }
            }
        }
    )
}
