package com.example.securescan.data.models

data class NewsItem(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val date: String = "",
    val imageRes: String = "",
    val tag: String = "",
    val tagColor: String = "",
    val readTime: Int = 0,
    val isFeatured: Boolean = false,
    val content: String = "",
    val createBy: String = "",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0
)