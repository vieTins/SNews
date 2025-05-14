package com.example.securescan.data.repository

import android.util.Log
import com.example.securescan.data.models.NotificationItem
import com.example.securescan.data.models.NotificationType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var listenerRegistration: ListenerRegistration? = null

    fun getNotifications(): Flow<List<NotificationItem>> = callbackFlow {
        listenerRegistration = notificationsCollection
            .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotificationRepository", "Error listening for notifications", error)
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        NotificationItem(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            time = doc.getString("time") ?: "",
                            isRead = doc.getBoolean("isRead") ?: false,
                            type = NotificationType.valueOf(doc.getString("type") ?: "NEWS"),
                            newsId = doc.getString("newsId")
                        )
                    } catch (e: Exception) {
                        Log.e("NotificationRepository", "Error parsing notification", e)
                        null
                    }
                } ?: emptyList()

                trySend(notifications)
            }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

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

    suspend fun markAsRead(notificationId: String) {
        try {
            notificationsCollection.document(notificationId)
                .update("isRead", true)
                .await()
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error marking notification as read", e)
        }
    }

    suspend fun deleteNotification(notificationId: String) {
        try {
            notificationsCollection.document(notificationId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error deleting notification", e)
        }
    }

    suspend fun markAllAsRead() {
        try {
            val batch = db.batch()
            val docs = notificationsCollection
                .whereEqualTo("isRead", false)
                .get()
                .await()
                .documents

            docs.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error marking all notifications as read", e)
        }
    }
}

