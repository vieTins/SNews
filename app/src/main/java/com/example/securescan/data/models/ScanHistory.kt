package com.example.securescan.data.models

data class ScanHistory(
    val userId: String = "",
    val type: String = "",
    val target: String = "",
    val result: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// Enum class để định nghĩa các loại quét
enum class ScanType {
    PHONE,
    BANK_ACCOUNT,
    WEBSITE,
    FILE
}

// Enum class để định nghĩa các kết quả quét
enum class ScanResult {
    FRAUD,
    SUSPICIOUS,
    DANGEROUS,
    NO_INFO
}
