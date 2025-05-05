package com.example.securescan.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.securescan.viewmodel.ThemeViewModel

private val DarkColorScheme = darkColorScheme(
    // Thay đổi màu primary và primaryContainer thành màu đen khi ở chế độ tối
    primary = Color(0xFF000000),
    onPrimary = TextPrimaryDark.copy(alpha = 0.9f),  // Điều chỉnh độ sáng của chữ để dịu hơn
    primaryContainer = Color(0xFF121212),  // Màu đen nhạt hơn một chút
    onPrimaryContainer = TextPrimaryDark.copy(alpha = 0.85f),

    // Giữ các màu accent và secondary
    secondary = accentBlue1,
    onSecondary = TextPrimaryDark.copy(alpha = 0.9f),
    secondaryContainer = DeepBlue,
    onSecondaryContainer = TextPrimaryDark.copy(alpha = 0.85f),
    tertiary = accentBlue2,
    onTertiary = TextPrimaryDark.copy(alpha = 0.9f),
    tertiaryContainer = DeepBlue,
    onTertiaryContainer = TextPrimaryDark.copy(alpha = 0.85f),

    // Màu nền đen
    background = Color(0xFF000000),  // Màu đen cho nền
    onBackground = TextPrimaryDark.copy(alpha = 0.87f),  // Chữ dịu hơn
    surface = Color(0xFF121212),  // Màu đen nhạt hơn một chút cho các bề mặt
    onSurface = TextPrimaryDark.copy(alpha = 0.87f),
    surfaceVariant = Color(0xFF1E1E1E),  // Màu đen nhạt hơn cho bề mặt biến thể
    onSurfaceVariant = TextPrimaryDark.copy(alpha = 0.75f),

    // Màu lỗi và cảnh báo
    error = ErrorRed,
    onError = TextPrimaryDark.copy(alpha = 0.9f),
    errorContainer = FunctionRedDark,
    onErrorContainer = TextPrimaryDark.copy(alpha = 0.9f)
)

private val LightColorScheme = lightColorScheme(
    primary = baseBlue3,
    onPrimary = White,
    primaryContainer = baseBlue2,
    onPrimaryContainer = White,
    secondary = accentBlue1,
    onSecondary = White,
    secondaryContainer = DeepBlue,
    onSecondaryContainer = White,
    tertiary = accentBlue2,
    onTertiary = White,
    tertiaryContainer = DeepBlue,
    onTertiaryContainer = White,
    background = BackgroundColor,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.12f),
    onErrorContainer = ErrorRed,
)

@Composable
fun AppTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    MaterialTheme(
        colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content
    )
}