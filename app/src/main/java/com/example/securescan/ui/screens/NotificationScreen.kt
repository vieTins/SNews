package com.example.securescan.ui.screens

import android.util.Log
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.securescan.data.models.NotificationItem
import com.example.securescan.data.models.NotificationType
import com.example.securescan.ui.theme.AccentBlue
import com.example.securescan.ui.theme.BackgroundColor
import com.example.securescan.ui.theme.DeepBlue
import com.example.securescan.ui.theme.ErrorRed
import com.example.securescan.ui.theme.LightBlue
import com.example.securescan.ui.theme.PaleBlue
import com.example.securescan.ui.theme.PrimaryBlue
import com.example.securescan.ui.theme.White
import com.example.securescan.viewmodel.NotificationViewModel
import kotlinx.coroutines.delay


@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = viewModel()
) {
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
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

    Log.d("NotificationScreen", "Notifications: $notifications")
    Log.d("NotificationScreen", "Selected filter: $selectedFilter")
    Log.d("NotificationScreen", "Filtered notifications: $filteredNotifications")

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            delay(2000)
            showSnackbar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top app bar
            NotificationTopAppBar(
                unreadCount = notifications.count { !it.isRead },
                onMarkAllRead = {
                    viewModel.markAllAsRead()
                    snackbarMessage = "Đã đánh dấu tất cả là đã đọc"
                    showSnackbar = true
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
                            // Đánh dấu thông báo đã đọc khi click
                            viewModel.markAsRead(notificationId)

                            // Nếu là thông báo tin tức, điều hướng đến bài viết
                            if (notification.newsId != null) {
                                navController.navigate("news_detail/${notification.newsId}")
                            }

                            // Hiển thị thông báo
                            snackbarMessage = "Đã mở thông báo: ${notification.title}"
                            showSnackbar = true
                        },
                        onDeleteClick = { notificationId ->
                            // Xóa thông báo
                            viewModel.deleteNotification(notificationId)
                            snackbarMessage = "Đã xóa thông báo"
                            showSnackbar = true
                        }
                    )
                }

                // Hiển thị khi không có thông báo
                if (filteredNotifications.isEmpty()) {
                    item {
                        EmptyNotifications()
                    }
                }
            }
        }

        // FAB để đăng ký nhận thông báo
        FloatingActionButton(
            onClick = {
                snackbarMessage = "Đã đăng ký nhận thông báo"
                showSnackbar = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = AccentBlue,
            contentColor = White
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Đăng ký nhận thông báo"
            )
        }

        // Thông báo snackbar
        AnimatedVisibility(
            visible = showSnackbar,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF323232))
            ) {
                Text(
                    text = snackbarMessage,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun NotificationTopAppBar(unreadCount: Int, onMarkAllRead: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepBlue, PrimaryBlue)
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notification icon với hiệu ứng ánh sáng
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(LightBlue.copy(alpha = 0.8f), LightBlue.copy(alpha = 0.1f)),
                                radius = 20f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications Icon",
                        tint = White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Title with unread count
                Column {
                    Text(
                        text = "Thông báo",
                        color = White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (unreadCount > 0) {
                        Text(
                            text = "$unreadCount thông báo chưa đọc",
                            color = White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Mark all as read button
            if (unreadCount > 0) {
                IconButton(
                    onClick = onMarkAllRead,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Đánh dấu tất cả đã đọc",
                        tint = White
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
        containerColor = BackgroundColor,
        contentColor = PrimaryBlue,
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
                        containerColor = if (selectedFilter == index) AccentBlue else White
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (selectedFilter == index) White else Color.Gray,
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
    val backgroundColor = if (!notification.isRead) PaleBlue else White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
            // Icon dựa trên loại thông báo
            NotificationTypeIcon(type = notification.type)

            Spacer(modifier = Modifier.width(16.dp))

            // Nội dung thông báo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontSize = 16.sp,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium,
                    color = DeepBlue
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Nút xóa thông báo
            IconButton(
                onClick = { onDeleteClick(notification.id) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa thông báo",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Indicator cho thông báo chưa đọc
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
fun EmptyNotifications() {
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
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bạn sẽ nhận được thông báo khi có tin tức mới",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { /* Xử lý cài đặt thông báo */ },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AccentBlue
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(AccentBlue)
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

