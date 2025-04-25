package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType,
    val newsId: Int? = null // ID để liên kết với tin tức cụ thể
)

enum class NotificationType {
    NEWS, WARNING, UPDATE, SECURITY
}

@Composable
fun NotificationScreen() {
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Danh sách thông báo mẫu
    var notifications by remember {
        mutableStateOf(
            listOf(
                NotificationItem(
                    1,
                    "Cảnh báo mới",
                    "Cảnh báo chiêu trò lừa đảo mới qua điện thoại kết hợp mạng xã hội",
                    "22/03/2025 09:30",
                    false,
                    NotificationType.WARNING,
                    1
                ),
                NotificationItem(
                    2,
                    "Hướng dẫn mới",
                    "Hướng dẫn nhận biết và bảo vệ khỏi tấn công Phishing mới nhất",
                    "20/03/2025 15:45",
                    true,
                    NotificationType.NEWS,
                    2
                ),
                NotificationItem(
                    3,
                    "Cập nhật an ninh",
                    "Phát hiện 5 ứng dụng giả mạo ngân hàng đang lây lan trên các kho ứng dụng",
                    "18/03/2025 11:20",
                    false,
                    NotificationType.WARNING,
                    3
                ),
                NotificationItem(
                    4,
                    "Cập nhật bảo mật",
                    "Cập nhật ngay: Lỗ hổng bảo mật nghiêm trọng trên các thiết bị Android",
                    "15/03/2025 08:15",
                    true,
                    NotificationType.UPDATE,
                    4
                ),
                NotificationItem(
                    5,
                    "Báo cáo mới",
                    "Báo cáo xu hướng lừa đảo trực tuyến quý 1 năm 2025 đã được cập nhật",
                    "12/03/2025 14:50",
                    false,
                    NotificationType.NEWS,
                    5
                ),
                NotificationItem(
                    6,
                    "Kiểm tra bảo mật",
                    "Nhắc nhở: Kiểm tra thiết lập bảo mật của tài khoản mạng xã hội thường xuyên",
                    "10/03/2025 16:30",
                    true,
                    NotificationType.SECURITY,
                    null
                )
            )
        )
    }

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
                    notifications = notifications.map { it.copy(isRead = true) }
                    snackbarMessage = "Đã đánh dấu tất cả là đã đọc"
                    showSnackbar = true
                }
            )

            // Các tùy chọn lọc thông báo
            NotificationFilterOptions()

            // Danh sách thông báo
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItemCard(
                        notification = notification,
                        onNotificationClick = { notificationId ->
                            // Đánh dấu thông báo đã đọc khi click
                            notifications = notifications.map {
                                if (it.id == notificationId) it.copy(isRead = true) else it
                            }

                            // Hiển thị thông báo
                            snackbarMessage = "Đã mở thông báo: ${notification.title}"
                            showSnackbar = true
                        },
                        onDeleteClick = { notificationId ->
                            // Xóa thông báo
                            notifications = notifications.filter { it.id != notificationId }
                            snackbarMessage = "Đã xóa thông báo"
                            showSnackbar = true
                        }
                    )
                }

                // Hiển thị khi không có thông báo
                if (notifications.isEmpty()) {
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
fun NotificationFilterOptions() {
    var selectedFilter by remember { mutableIntStateOf(0) }
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
                onClick = { selectedFilter = index },
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
    onNotificationClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
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
        NotificationType.WARNING -> Icons.Default.Warning to Red
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

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun NotificationScreenPreview() {
    NotificationScreen()
}