package com.example.securescan.data.models

data class RssNewsItem(
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String,
    val author: String,
    val imageUrl: String? = null,
    val guid: String
) 