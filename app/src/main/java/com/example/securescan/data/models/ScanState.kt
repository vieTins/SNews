package com.example.securescan.data.models

sealed class ScanState {
    object Idle : ScanState() // Khi chưa bắt đầu quét
    object Scanning : ScanState() // Khi đang quét
    data class Result(
        val isMalicious: Boolean, // Kết quả có phải là phần mềm độc hại không
        val message: String, // Thông báo về kết quả quét
        val details: String // Chi tiết về kết quả quét
    ) : ScanState() // Kết quả quét
}