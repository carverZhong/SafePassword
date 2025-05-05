package com.github.carver.safepassword.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // 主色：浅紫色
    secondary = Color(0xFF03DAC5), // 次色：蓝绿色
    background = Color(0xFF121212), // 背景色：深灰色
    surface = Color(0xFF121212), // 表面色：深灰色
    onPrimary = Color(0xFF000000), // 主色上的文本颜色：黑色
    onSecondary = Color(0xFF000000), // 次色上的文本颜色：黑色
    onBackground = Color(0xFFFFFFFF), // 背景上的文本颜色：白色
    onSurface = Color(0xFFFFFFFF) // 表面上的文本颜色：白色
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB82132), // 主色：浅蓝色，清新自然
    primaryContainer = Color(0xFFD2665A), // 主色变体：稍深的蓝色
    secondary = Color(0xFFF2B28C), // 次色：薄荷绿，柔和清新
    secondaryContainer = Color(0xFFF6DED8), // 次色变体：稍深的薄荷绿
    background = Color(0xFFF5F5F5), // 背景色：浅灰色，干净清爽
    surface = Color(0xFFFFFFFF), // 表面色：白色
    onPrimary = Color(0xFFFFFFFF), // 主色上的文本颜色：白色
    onSecondary = Color(0xFF000000), // 次色上的文本颜色：黑色
    onBackground = Color(0xFF000000), // 背景上的文本颜色：黑色
    onSurface = Color(0xFF000000) // 表面上的文本颜色：黑色
)
@Composable
fun SafePasswordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun getColorTheme(): ColorScheme {
    return if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
}