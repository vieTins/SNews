package com.example.securescan.data.models

data class BookmarkPost(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
) 