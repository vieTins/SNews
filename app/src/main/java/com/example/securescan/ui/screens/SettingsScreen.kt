package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.securescan.data.models.User
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.ThemeViewModel
import com.example.securescan.viewmodel.UserViewModel
import com.example.securescan.ui.components.CustomSwitch

@Composable
fun SettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    onLogout: () -> Unit
) {
    val viewModel = remember { UserViewModel() }
    val user = viewModel.user
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Fixed AppTopBar
            AppTopBar(
                title = "Cài đặt",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationClick = { navController.popBackStack() }
            )

            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Modern header with gradient background
                SettingsHeader(user = user.value, navController = navController, isDarkMode = isDarkMode)

                Spacer(modifier = Modifier.height(8.dp))

                // Settings sections
                ExpandableSettingsCategory(
                    title = "Giao diện",
                    icon = Icons.Default.Brush,
                    initiallyExpanded = true
                ) {
                    SettingsToggleItem(
                        icon = Icons.Default.DarkMode,
                        title = "Chế độ tối",
                        subtitle = "Thay đổi giao diện sang màu tối",
                        isChecked = isDarkMode,
                        onCheckedChange = { themeViewModel.toggleTheme() }
                    )
                }

                ExpandableSettingsCategory(
                    title = "Thông tin ứng dụng",
                    icon = Icons.Outlined.Info,
                    initiallyExpanded = false
                ) {
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Câu hỏi thường gặp",
                        subtitle = "Trợ giúp và hướng dẫn sử dụng",
                        onClick = { navController.navigate("about_app/${AboutSection.FAQ}") }
                    )

                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Giới thiệu",
                        subtitle = "Thông tin về Secure Scan",
                        onClick = { navController.navigate("about_app/${AboutSection.ABOUT}") }
                    )

                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "Điều khoản và thỏa thuận sử dụng",
                        subtitle = "Chính sách bảo mật và điều khoản",
                        onClick = { navController.navigate("about_app/${AboutSection.TERMS}") }
                    )

                    SettingsItem(
                        icon = Icons.Default.Star,
                        title = "Đánh giá ứng dụng",
                        subtitle = "Chia sẻ ý kiến của bạn",
                        onClick = { navController.navigate("about_app/${AboutSection.RATE}") }
                    )
                }

                ExpandableSettingsCategory(
                    title = "Cài đặt khác",
                    icon = Icons.Default.Settings,
                    initiallyExpanded = false
                ) {
                    LanguageSettingsItem()

                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Thông báo",
                        subtitle = "Quản lý thông báo và cảnh báo",
                        onClick = {
                            navController.navigate("notification_settings") {
                                popUpTo("notification_screen") {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Security,
                        title = "Mật khẩu",
                        subtitle = "Cập nhật mật khẩu và bảo mật",
                        onClick = { /* TODO: Implement security settings */ }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logout button
                LogoutButton(onLogout = onLogout)

                Spacer(modifier = Modifier.height(24.dp))

                // Version info
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Secure Scan v1.0.0",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsHeader(
    user: User,
    navController: NavController,
    isDarkMode: Boolean
) {
    val gradientColors = if (isDarkMode) {
        listOf(
            baseBlue3.copy(alpha = 0.8f),
            baseBlue3.copy(alpha = 0.6f),
            baseBlue3.copy(alpha = 0.3f)
        )
    } else {
        listOf(
            baseBlue3,
            baseBlue3.copy(alpha = 0.7f),
            baseBlue3.copy(alpha = 0.4f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = gradientColors
                )
            )
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile avatar with shadow and caching
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (user.profilePic != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.profilePic)
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .build(),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Profile",
                        tint = baseBlue3,
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User info
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Edit profile button
            FilledTonalButton(
                onClick = { navController.navigate("edit_profile") },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Quản lý tài khoản",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ExpandableSettingsCategory(
    title: String,
    icon: ImageVector,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(300)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Category header - clickable to expand/collapse
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = baseBlue3,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotationState)
                    )
                }

                // Animated expanding content
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(300)
                    ) + fadeIn(initialAlpha = 0.3f),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(300)
                    ) + fadeOut()
                ) {
                    Column {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = baseBlue3,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
}

@Composable
fun LanguageSettingsItem() {
    var expandLanguageOptions by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Tiếng Việt") }

    Column {
        // Language header item
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandLanguageOptions = !expandLanguageOptions }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Ngôn ngữ",
                tint = baseBlue3,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Ngôn ngữ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Thay đổi ngôn ngữ hiển thị: $selectedLanguage",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = if (expandLanguageOptions)
                    Icons.Default.KeyboardArrowDown else Icons.Default.ArrowForwardIos,
                contentDescription = if (expandLanguageOptions) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(16.dp)
                    .rotate(if (expandLanguageOptions) 180f else 0f)
            )
        }

        // Language options
        AnimatedVisibility(
            visible = expandLanguageOptions,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 16.dp)
            ) {
                // Vietnamese option
                LanguageOption(
                    language = "Tiếng Việt",
                    isSelected = selectedLanguage == "Tiếng Việt",
                    onSelect = {
                        selectedLanguage = "Tiếng Việt"
                        expandLanguageOptions = false
                    }
                )

                // English option
                LanguageOption(
                    language = "English",
                    isSelected = selectedLanguage == "English",
                    onSelect = {
                        selectedLanguage = "English"
                        expandLanguageOptions = false
                    }
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp, end = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = language,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) baseBlue3 else MaterialTheme.colorScheme.onSurface
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = baseBlue3,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = baseBlue3,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        CustomSwitch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            color = baseBlue3
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    )
}

@Composable
fun LogoutButton(
    onLogout: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Xác nhận đăng xuất",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    "Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Đăng xuất")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Đăng xuất",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}