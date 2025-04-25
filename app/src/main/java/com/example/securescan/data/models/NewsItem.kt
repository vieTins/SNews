package com.example.securescan.data.models

import androidx.compose.ui.graphics.Color

data class NewsItem(
    val id: Int = 0,
    val title: String = "",
    val summary: String = "",
    val date: String = "",
    val imageRes: String = "",
    val tag: String = "",
    val tagColor : String = "",
    val readTime: Int = 0,
    val isFeatured: Boolean = false,
    val content : String = "",
    val createBy : String = "",
)