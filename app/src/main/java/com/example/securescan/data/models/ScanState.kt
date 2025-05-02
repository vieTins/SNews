package com.example.securescan.data.models

sealed class ScanState {
    data object Idle : ScanState()
    data object Scanning : ScanState()
    data class Result(
        val isMalicious: Boolean,
        val message: String,
        val details: String
    ) : ScanState()
}