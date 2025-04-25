package com.example.securescan.data.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ScanHistory(
    val type: String = "",
    val scanTarget: String = "",
    @ServerTimestamp val timestamp: Date? = null,
    val isSafe: Boolean = true,
    val threatLevel: String = "",
    val details: ScanDetails = ScanDetails()
)

data class ScanDetails(
    val engineResults: List<String> = listOf(),
    val detectionRatio: String = "0/60"
)
