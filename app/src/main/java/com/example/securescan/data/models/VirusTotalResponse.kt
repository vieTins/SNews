package com.example.securescan.data.models

data class VirusTotalResponse(
    val data: Data
)

data class Data(
    val id: String,
    val type: String,
    val attributes: Attributes?
)

data class Attributes(
    val status: String,
    val stats: Stats? = null,
    val results: Map<String, ScannerResult> = emptyMap() // sử dụng Map để lưu trữ kết quả từ nhiều scanner
)

data class Stats(
    val harmless: Int,
    val malicious: Int,
    val suspicious: Int,
    val undetected: Int,
    val timeout: Int = 0
)

data class ScannerResult(
    val category: String,
    val result: String?,
    val method: String?,
    val engine_name: String?
)

data class ScanResultResponse(
    val data: Data
)