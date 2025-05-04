package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.securescan.data.models.NotificationType
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.components.CustomSwitch
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.NotificationSettingsViewModel
import kotlinx.coroutines.delay

@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    viewModel: NotificationSettingsViewModel = viewModel()
) {
    val settings by viewModel.notificationSettings.collectAsState()
    var showSnackBar by remember { mutableStateOf(false) }
    var snackBarMessage by remember { mutableStateOf("") }

    // Hide snackbar after 3 seconds
    LaunchedEffect(showSnackBar) {
        if (showSnackBar) {
            delay(3000)
            showSnackBar = false
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Cài đặt thông báo",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Tổng quan về thông báo
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (settings.allNotificationsEnabled)
                                    Icons.Rounded.Notifications
                                else
                                    Icons.Rounded.NotificationsOff,
                                contentDescription = null,
                                tint = baseBlue3,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Tổng quan",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Tất cả thông báo",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )

                                Text(
                                    text = if (settings.allNotificationsEnabled)
                                        "Đang hoạt động"
                                    else
                                        "Đang tắt",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (settings.allNotificationsEnabled)
                                        baseBlue3
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            CustomSwitch(
                                checked = settings.allNotificationsEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.toggleAllNotifications(enabled)
                                    snackBarMessage = if (enabled)
                                        "Đã bật tất cả thông báo"
                                    else
                                        "Đã tắt tất cả thông báo"
                                    showSnackBar = true
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Các loại thông báo
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Loại thông báo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        NotificationTypeSetting(
                            title = "Tin tức",
                            description = "Thông báo về tin tức mới",
                            icon = Icons.Rounded.Notifications,
                            enabled = settings.newsEnabled,
                            color = baseBlue3,
                            onToggle = { enabled ->
                                viewModel.toggleNotificationType(NotificationType.NEWS, enabled)
                                snackBarMessage = if (enabled)
                                    "Đã bật thông báo tin tức"
                                else
                                    "Đã tắt thông báo tin tức"
                                showSnackBar = true
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        NotificationTypeSetting(
                            title = "Cảnh báo",
                            description = "Thông báo về các cảnh báo bảo mật",
                            icon = Icons.Rounded.Warning,
                            enabled = settings.warningEnabled,
                            color = Color(0xFFE57373),
                            onToggle = { enabled ->
                                viewModel.toggleNotificationType(NotificationType.WARNING, enabled)
                                snackBarMessage = if (enabled)
                                    "Đã bật thông báo cảnh báo"
                                else
                                    "Đã tắt thông báo cảnh báo"
                                showSnackBar = true
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        NotificationTypeSetting(
                            title = "Cập nhật",
                            description = "Thông báo về cập nhật ứng dụng",
                            icon = Icons.Rounded.Update,
                            enabled = settings.updateEnabled,
                            color = Color(0xFF4FC3F7),
                            onToggle = { enabled ->
                                viewModel.toggleNotificationType(NotificationType.UPDATE, enabled)
                                snackBarMessage = if (enabled)
                                    "Đã bật thông báo cập nhật"
                                else
                                    "Đã tắt thông báo cập nhật"
                                showSnackBar = true
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        NotificationTypeSetting(
                            title = "Bảo mật",
                            description = "Thông báo về các vấn đề bảo mật",
                            icon = Icons.Rounded.Security,
                            enabled = settings.securityEnabled,
                            color = Color(0xFF81C784),
                            onToggle = { enabled ->
                                viewModel.toggleNotificationType(NotificationType.SECURITY, enabled)
                                snackBarMessage = if (enabled)
                                    "Đã bật thông báo bảo mật"
                                else
                                    "Đã tắt thông báo bảo mật"
                                showSnackBar = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            // Animated SnackBar with improved design
            AnimatedVisibility(
                visible = showSnackBar,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.BottomCenter),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(durationMillis = 250))
            ) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (snackBarMessage.contains("bật"))
                                    Icons.Rounded.Notifications
                                else
                                    Icons.Rounded.NotificationsOff,
                                contentDescription = null,
                                tint = baseBlue3,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = snackBarMessage,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationTypeSetting(
    title: String,
    description: String,
    icon: ImageVector,
    enabled: Boolean,
    color: Color = baseBlue3,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Modern icon with gradient background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        CustomSwitch(
            checked = enabled,
            onCheckedChange = onToggle,
            color = color
        )
    }
}