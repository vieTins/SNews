package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.securescan.data.models.NotificationItem
import com.example.securescan.data.models.NotificationType
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.theme.AccentBlue
import com.example.securescan.ui.theme.DeepBlue
import com.example.securescan.ui.theme.ErrorRed
import com.example.securescan.ui.theme.PaleBlue
import com.example.securescan.ui.theme.White
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.NotificationViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = viewModel()
) {
    var showSnackBar by remember { mutableStateOf(false) }
    var snackBarMessage by remember { mutableStateOf("") }
    val notifications by viewModel.notifications.collectAsState()
    var selectedFilter by remember { mutableIntStateOf(0) }

    // Lọc thông báo dựa trên tab được chọn
    val filteredNotifications = when (selectedFilter) {
        0 -> notifications // Tất cả
        1 -> notifications.filter { !it.isRead } // Chưa đọc
        2 -> notifications.filter { it.type == NotificationType.WARNING } // Cảnh báo
        3 -> notifications.filter { it.type == NotificationType.NEWS } // Tin tức
        4 -> notifications.filter { it.type == NotificationType.UPDATE } // Cập nhật
        else -> notifications
    }

    LaunchedEffect(showSnackBar) {
        if (showSnackBar) {
            delay(2000)
            showSnackBar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top app bar
            AppTopBar(
                title = "Thông báo",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationClick = { navController.popBackStack() },
                actionIcon = Icons.Default.DoneAll,
                onActionIconClick = {
                    viewModel.markAllAsRead()
                    snackBarMessage = "Đã đánh dấu tất cả là đã đọc"
                    showSnackBar = true
                },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Số thông báo chưa đọc
                        if (notifications.count { !it.isRead } > 0) {
                            Text(
                                text = "${notifications.count { !it.isRead }} chưa đọc",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            )

            // Các tùy chọn lọc thông báo
            NotificationFilterOptions(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            // Danh sách thông báo đã lọc
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredNotifications) { notification ->
                    NotificationItemCard(
                        notification = notification,
                        onNotificationClick = { notificationId ->
                            viewModel.markAsRead(notificationId)
                            if (notification.newsId != null) {
                                navController.navigate("news_detail/${notification.newsId}")
                            }
                            snackBarMessage = "Đã mở thông báo: ${notification.title}"
                            showSnackBar = true
                        },
                        onDeleteClick = { notificationId ->
                            viewModel.deleteNotification(notificationId)
                            snackBarMessage = "Đã xóa thông báo"
                            showSnackBar = true
                        }
                    )
                }

                if (filteredNotifications.isEmpty()) {
                    item {
                        EmptyNotifications(
                            onSettingsClick = {
                                // TODO: Track notification settings click event
                                // Event: User clicked on notification settings from empty notifications screen
                                // You can add your event tracking logic here
                                
                                navController.navigate("notification_settings") {
                                    popUpTo("notification_screen") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }

        // SnackBar
        AnimatedVisibility(
            visible = showSnackBar,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        tint = baseBlue3,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = snackBarMessage,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationFilterOptions(
    selectedFilter: Int,
    onFilterSelected: (Int) -> Unit
) {
    val filters = listOf("Tất cả", "Chưa đọc", "Cảnh báo", "Tin tức", "Cập nhật")

    ScrollableTabRow(
        selectedTabIndex = selectedFilter,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        filters.forEachIndexed { index, filter ->
            Tab(
                selected = selectedFilter == index,
                onClick = { onFilterSelected(index) },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedFilter == index) 
                            baseBlue3
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (selectedFilter == index) 
                            MaterialTheme.colorScheme.onPrimary
                        else 
                            MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selectedFilter == index) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: NotificationItem,
    onNotificationClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val backgroundColor = if (!notification.isRead) PaleBlue else White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Main card content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNotificationClick(notification.id) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon based on notification type
                NotificationTypeIcon(type = notification.type)

                Spacer(modifier = Modifier.width(16.dp))

                // Notification content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 13.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium,
                        color = DeepBlue
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formatNotificationTime(notification.time),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Unread indicator
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentBlue)
                    )
                }
            }
        }

        // Delete icon in top-right corner
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Xóa thông báo",
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 24.dp)
                .size(18.dp)
                .clickable { showDialog = true }
        )

        // Confirmation dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        "Xóa thông báo",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Text(
                        "Bạn có chắc muốn xóa thông báo này?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            onDeleteClick(notification.id)
                        }
                    ) {
                        Text(
                            "Xóa",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
fun NotificationTypeIcon(type: NotificationType) {
    val (icon, color) = when (type) {
        NotificationType.WARNING -> Icons.Default.Warning to ErrorRed
        NotificationType.NEWS -> Icons.AutoMirrored.Filled.Article to AccentBlue
        NotificationType.UPDATE -> Icons.Default.Update to Color(0xFF8BC34A)
        NotificationType.SECURITY -> Icons.Default.Security to Color(0xFF7E57C2)
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun EmptyNotifications(
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsOff,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Không có thông báo",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bạn sẽ nhận được thông báo khi có tin tức mới",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onSettingsClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = baseBlue3
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(baseBlue3)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Cài đặt thông báo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatNotificationTime(time: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(time)
        date?.let { outputFormat.format(it) } ?: time
    } catch (e: Exception) {
        time
    }
}

