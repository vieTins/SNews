package com.example.securescan.data.repository

import android.util.Log
import com.example.securescan.data.models.NewsItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

class NewsRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val newsCollection = firestore.collection("posts")

    // Helper để convert hex string thành Color
    private fun String.toColor(): Color {
        return try {
            Color(this.toColorInt())
        } catch (e: Exception) {
            Color.Black
        }
    }

    // Lấy toàn bộ tin tức
    fun getAllNews(): Flow<List<NewsItem>> = callbackFlow {
        val listener = newsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("NewsRepository", "Error getting news", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            val newsList = snapshot?.documents?.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    NewsItem(
                        id = data["id"] as? String ?: "",
                        title = data["title"] as? String ?: "",
                        summary = data["summary"] as? String ?: "",
                        date = data["date"] as? String ?: "",
                        imageRes = data["imageRes"] as? String ?: "",
                        tag = data["tag"] as? String ?: "",
                        tagColor = data["tagColor"] as? String ?: "",
                        readTime = (data["readTime"] as? Long)?.toInt() ?: 0,
                        isFeatured = (data["isFeatured"] as? Boolean) ?: false,
                        content = data["content"] as? String ?: "",
                        createBy = data["createdBy"] as? String ?: "",
                        likeCount = (data["likeCount"] as? Long)?.toInt() ?: 0,
                        commentCount = (data["commentCount"] as? Long)?.toInt() ?: 0,
                        shareCount = (data["shareCount"] as? Long)?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
            trySend(newsList)
        }

        awaitClose { listener.remove() }
    }

    // Tìm kiếm
    fun searchNews(query: String): Flow<List<NewsItem>> {
        return getAllNews().map { newsList ->
            if (query.isBlank()) {
                newsList
            } else {
                newsList.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.summary.contains(query, ignoreCase = true)
                }
            }
        }
    }

    // Lấy 3 tin nổi bật
    fun getFeaturedNews(): Flow<List<NewsItem>> {
        return getAllNews().map { it.take(3) }
    }

    // Lấy tin theo ID
    fun getNewsById(id: String): Flow<NewsItem?> {
        return getAllNews().map { list -> list.find {
            it.id.equals(id, ignoreCase = true)
        } }
    }
}
