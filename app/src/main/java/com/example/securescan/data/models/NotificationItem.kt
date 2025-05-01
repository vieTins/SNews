package com.example.securescan.data.models

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType,
    val newsId: String? = null // ID để liên kết với tin tức cụ thể
)

enum class NotificationType {
    NEWS,
    WARNING,
    UPDATE,
    SECURITY
}