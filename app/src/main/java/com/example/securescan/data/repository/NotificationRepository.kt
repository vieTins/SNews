package com.example.securescan.data.repository

import android.util.Log
import com.example.securescan.data.models.NotificationItem
import com.example.securescan.data.models.NotificationType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())


    // thêm thông báo vào Firestore
    suspend fun addNotification(
        title: String,
        message: String,
        type: NotificationType,
        newsId: String? = null
    ) {
        val notification = hashMapOf(
            "title" to title,
            "message" to message,
            "time" to dateFormat.format(Date()),
            "isRead" to false,
            "type" to type.name,
            "newsId" to newsId
        )
        notificationsCollection.add(notification).await()
    }

    fun getNotifications(): Flow<List<NotificationItem>> = flow {
        val snapshot = notificationsCollection.get().await()
        val notifications = snapshot.documents.mapNotNull { doc ->
            try {
                val timeMillis = doc.getString("time")?.toLongOrNull()
                val formattedTime = timeMillis?.let { dateFormat.format(Date(it)) } ?: ""

                NotificationItem(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    message = doc.getString("message") ?: "",
                    time = formattedTime,
                    isRead = doc.getBoolean("isRead") ?: false,
                    type = NotificationType.valueOf(doc.getString("type") ?: "NEWS"),
                    newsId = doc.getString("newsId")
                )
            } catch (e: Exception) {
                null
            }
        }
        emit(notifications)
    }

    suspend fun markAsRead(notificationId: String) {
        val doc = notificationsCollection.whereEqualTo("id", notificationId).get().await().documents.firstOrNull()
        Log.d("NotificationRepository", "Marking notification as read: $notificationId")
        doc?.reference?.update("isRead", true)?.await()
    }

    suspend fun deleteNotification(notificationId: String) {
        Log.d("NotificationRepository", "Deleting notification with ID: $notificationId")
        val doc = notificationsCollection.whereEqualTo("id", notificationId).get().await().documents.firstOrNull()
        doc?.reference?.delete()?.await()
    }

    suspend fun markAllAsRead() {
        val batch = db.batch()  // Tạo một batch để thực hiện nhiều thao tác cùng lúc
        val docs = notificationsCollection.whereEqualTo("isRead", false).get().await().documents // Lấy tất cả các thông báo chưa đọc
        Log.d("NotificationRepository", "Marking ${docs.size} notifications as read")
        docs.forEach { doc ->
            batch.update(doc.reference, "isRead", true) // Đánh dấu là đã đọc
        }
        batch.commit().await()
    }
}

