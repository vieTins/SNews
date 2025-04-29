package com.example.securescan.data.models

data class CommentPost(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val userName : String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
