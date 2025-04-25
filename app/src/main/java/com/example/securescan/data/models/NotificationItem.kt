package com.example.securescan.data.models

import com.example.securescan.ui.screens.NotificationType

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
    NEWS,
    WARNING,
    UPDATE,
    SECURITY
}