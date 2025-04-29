package com.example.securescan.data.models

data class SharePost(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
