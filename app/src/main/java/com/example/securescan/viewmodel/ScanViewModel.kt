package com.example.securescan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.models.ScanHistory
import com.example.securescan.data.models.ScanType
import com.example.securescan.data.network.VirusTotalApiService
import com.example.securescan.data.repository.ScanRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ScanViewModel : ViewModel() {
    private val apiKey = "4aa49aa105d9fe124cbfb0f0073ea91a6194a6738ef735d5e112e605b78edacb"
    private val scanRepository = ScanRepository()
    private val _scanHistory = MutableStateFlow<List<ScanHistory>>(emptyList())
    val scanHistory: StateFlow<List<ScanHistory>> = _scanHistory

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.virustotal.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(VirusTotalApiService::class.java)

    fun loadScanHistory(userId: String) {
        viewModelScope.launch {
            try {
                val history = scanRepository.getScanHistoryByUserId(userId)
                _scanHistory.value = history
            } catch (_: Exception) {
            }
        }
    }

    fun scanFile(filePath: String, userId: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            onResult("Starting file scan...")

            try {
                val file = File(filePath)
                if (!file.exists()) {
                    onResult("Error: File does not exist")
                    return@launch
                }

                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = apiService.uploadFile(apiKey, body)

                if (response.isSuccessful && response.body() != null) {
                    val analysisId = response.body()?.data?.id ?: ""
                    if (analysisId.isEmpty()) {
                        onResult("Error: Could not get analysis ID")
                        return@launch
                    }

                    onResult("File uploaded successfully. Scanning in progress...")
                    val scanResult = waitForScanResult(analysisId)
                    
                    // Parse malicious count from result
                    val maliciousRegex = Regex("Malicious:\\s*(\\d+)", RegexOption.IGNORE_CASE)
                    val match = maliciousRegex.find(scanResult)
                    val maliciousCount = match?.groups?.get(1)?.value?.toIntOrNull() ?: 0
                    
                    // Save scan history
                    val scanHistory = ScanHistory(
                        userId = userId,
                        type = ScanType.FILE.name,
                        target = file.name,
                        result = scanRepository.determineScanResult(ScanType.FILE, maliciousCount)
                    )
                    scanRepository.saveScanHistory(scanHistory)
                    
                    // Reload scan history after saving
                    loadScanHistory(userId)
                    
                    onResult(scanResult)
                } else {
                    val errorCode = response.code()
                    response.errorBody()?.string() ?: "Unknown error"
                    onResult("Error uploading file. HTTP Status: $errorCode")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message ?: "Unknown error occurred"}")
            }
        }
    }

    fun scanUrl(url: String, userId: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            onResult("Starting URL scan...")

            val normalizedUrl = normalizeUrl(url)

            try {
                val response = apiService.scanUrl(apiKey, normalizedUrl)

                if (response.isSuccessful && response.body() != null) {
                    val analysisId = response.body()?.data?.id ?: ""
                    if (analysisId.isEmpty()) {
                        onResult("Error: Could not get analysis ID")
                        return@launch
                    }

                    val scanResult = waitForScanResult(analysisId)
                    
                    // Parse malicious count from result
                    val maliciousRegex = Regex("Malicious:\\s*(\\d+)", RegexOption.IGNORE_CASE)
                    val match = maliciousRegex.find(scanResult)
                    val maliciousCount = match?.groups?.get(1)?.value?.toIntOrNull() ?: 0
                    
                    // Save scan history
                    val scanHistory = ScanHistory(
                        userId = userId,
                        type = ScanType.WEBSITE.name,
                        target = normalizedUrl,
                        result = scanRepository.determineScanResult(ScanType.WEBSITE, maliciousCount)
                    )
                    scanRepository.saveScanHistory(scanHistory)
                    
                    // Reload scan history after saving
                    loadScanHistory(userId)
                    
                    onResult(scanResult)
                } else {
                    val errorCode = response.code()
                    response.errorBody()?.string() ?: "Unknown error"
                    onResult("Error submitting URL. HTTP Status: $errorCode")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message ?: "Unknown error occurred"}")
            }
        }
    }

    private fun normalizeUrl(input: String): String {
        var url = input.trim()

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url
        }

        // Kiểm tra xem có phải là địa chỉ IP không
        val ipPattern = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$"
        if (url.matches(Regex(ipPattern))) {
            return "http://$url"
        }

        // Xử lý domain không có scheme
        if (!url.contains("://")) {
            // Loại bỏ www. nếu có
            if (url.startsWith("www.")) {
                url = url.substring(4)
            }

            url = "http://$url"
        }

        return url
    }

    private suspend fun waitForScanResult(analysisId: String): String {
        val maxAttempts = 15
        val delayBetweenAttemptsMs = 5000L
        val totalTimeout = 120000L // 2 phút timeout

        return withTimeoutOrNull(totalTimeout) {
            var attempts = 0
            var lastStatus = ""

            while (attempts < maxAttempts) {
                attempts++

                try {
                    val response = apiService.getScanReport(apiKey, analysisId)

                    if (response.isSuccessful && response.body() != null) {
                        val attributes = response.body()?.data?.attributes
                        val status = attributes?.status ?: "unknown"
                        lastStatus = status

                        // Chờ đến khi scan hoàn tất
                        if (status != "completed") {
                            delay(delayBetweenAttemptsMs)
                            continue
                        }

                        val engineResults = attributes?.results
                        val stats = attributes?.stats
                        val totalEngines = engineResults?.size ?: 0
                        val scannedEngines = engineResults?.count { it.value.result != null } ?: 0

                        // Chờ cho đến khi ít nhất 90% engine đã scan
                        if (totalEngines > 0 && scannedEngines >= totalEngines * 0.9) {
                            val maliciousEngines = engineResults?.filter { it.value.category == "malicious" }
                            val suspiciousEngines = engineResults?.filter { it.value.category == "suspicious" }

                            val result = buildString {
                                append("Scan completed.\n\n")
                                append("Stats:\n")
                                append(" - Malicious: ${stats?.malicious}\n")
                                append(" - Suspicious: ${stats?.suspicious}\n")
                                append(" - Harmless: ${stats?.harmless}\n")
                                append(" - Undetected: ${stats?.undetected}\n")
                                append(" - Timeout: ${stats?.timeout}\n")
                                append(" - Total engines: $totalEngines\n")
                                append(" - Scanned engines: $scannedEngines\n\n")

                                if (maliciousEngines != null) {
                                    if (maliciousEngines.isNotEmpty()) {
                                        append("Detected by malicious engines:\n")
                                        maliciousEngines.forEach {
                                            append(" - ${it.key}: ${it.value.result}\n")
                                        }
                                    } else {
                                        append("No malicious engines detected this file/URL.\n")
                                    }
                                }

                                if (suspiciousEngines != null) {
                                    if (suspiciousEngines.isNotEmpty()) {
                                        append("\nSuspicious detections:\n")
                                        suspiciousEngines.forEach {
                                            append(" - ${it.key}: ${it.value.result}\n")
                                        }
                                    }
                                }
                            }

                            return@withTimeoutOrNull result
                        } else {
                            delay(delayBetweenAttemptsMs)
                            continue
                        }
                    } else {
                        delay(delayBetweenAttemptsMs)
                    }
                } catch (e: Exception) {
                    delay(delayBetweenAttemptsMs)
                }
            }

            return@withTimeoutOrNull "Scan did not complete in $maxAttempts attempts. Last status: $lastStatus"
        } ?: "Scan timed out after waiting ${totalTimeout / 1000} seconds."
    }
}