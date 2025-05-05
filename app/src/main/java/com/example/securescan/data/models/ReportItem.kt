package com.example.securescan.data.models

data class ReportItem (
    val id : String = "",
    val type: String = "", // "Phone", "Website", "Bank"
    val target: String = "",
    val description: String = "",
    val reportedBy: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val check: Boolean = false,
    val imageUrl: String = "",
)