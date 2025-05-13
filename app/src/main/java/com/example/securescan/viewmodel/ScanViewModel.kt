package com.example.securescan.viewmodel

import android.util.Log
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
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ScanViewModel : ViewModel() {
    companion object {
        private const val TAG = "ScanViewModel"
        private const val API_KEY = "11d799a8f97df12518c23911f9f9b8b081746cd07ca656cf6c1e3bcb01c1760b"
    }

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

                val response = apiService.uploadFile(API_KEY, body)

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
            var retryCount = 0
            val maxRetries = 3
            var lastError: String? = null

            while (retryCount < maxRetries) {
                try {
                    val response = apiService.scanUrl(API_KEY, normalizedUrl)
                    
                    Log.d(TAG, "Scan URL Response: ${response.code()} - ${response.message()}")
                    
                    when (response.code()) {
                        200, 201 -> {
                            if (response.body() != null) {
                                val analysisId = response.body()?.data?.id ?: ""
                                if (analysisId.isEmpty()) {
                                    onResult("Error: Could not get analysis ID")
                                    return@launch
                                }

                                onResult("URL submitted successfully. Scanning in progress...")
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
                                return@launch
                            }
                        }
                        409 -> {
                            // Conflict error - wait and retry
                            lastError = "Server is busy. Retrying..."
                            Log.d(TAG, "Conflict error, retrying in 5 seconds...")
                            delay(5000) // Wait 5 seconds before retry
                            retryCount++
                            continue
                        }
                        else -> {
                            val errorBody = response.errorBody()?.string()
                            lastError = "Error submitting URL. HTTP Status: ${response.code()}, Error: $errorBody"
                            Log.e(TAG, lastError)
                            break
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during URL scan", e)
                    lastError = "Error: ${e.message ?: "Unknown error occurred"}"
                    break
                }
            }

            onResult(lastError ?: "Failed to scan URL after $maxRetries attempts")
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
        val maxAttempts = 30
        val delayBetweenAttemptsMs = 5000L
        val totalTimeout = 180000L // 3 minutes

        return withTimeoutOrNull(totalTimeout) {
            var attempts = 0
            var lastStatus = ""
            var lastError: String? = null

            while (attempts < maxAttempts) {
                attempts++

                try {
                    val response = apiService.getScanReport(API_KEY, analysisId)
                    Log.d(TAG, "Get Report Response: ${response.code()} - ${response.message()}")
                    
                    if (response.isSuccessful && response.body() != null) {
                        val attributes = response.body()?.data?.attributes
                        val status = attributes?.status ?: "unknown"
                        lastStatus = status

                        Log.d(TAG, "Current scan status: $status")
                        
                        when (status) {
                            "completed" -> {
                                val engineResults = attributes?.results
                                val stats = attributes?.stats
                                val totalEngines = engineResults?.size ?: 0
                                val scannedEngines = engineResults?.count { it.value.result != null } ?: 0

                                val result = buildString {
                                    append("Scan completed.\n\n")
                                    append("Stats:\n")
                                    append(" - Malicious: ${stats?.malicious}\n")
                                    append(" - Suspicious: ${stats?.suspicious}\n")
                                    append(" - Harmless: ${stats?.harmless}\n")
                                    append(" - Undetected: ${stats?.undetected}\n")
                                    append(" - Timeout: ${stats?.timeout}\n")
                                    append(" - Total engines: $totalEngines\n")
                                    append(" - Scanned engines: $scannedEngines\n")
                                    append(" - Scan progress: ${(scannedEngines.toFloat() / totalEngines * 100).toInt()}%\n\n")

                                    if (engineResults != null) {
                                        val maliciousEngines = engineResults.filter { it.value.category == "malicious" }
                                        val suspiciousEngines = engineResults.filter { it.value.category == "suspicious" }

                                        if (maliciousEngines.isNotEmpty()) {
                                            append("Detected by malicious engines:\n")
                                            maliciousEngines.forEach {
                                                append(" - ${it.key}: ${it.value.result}\n")
                                            }
                                        } else {
                                            append("No malicious engines detected this file/URL.\n")
                                        }

                                        if (suspiciousEngines.isNotEmpty()) {
                                            append("\nSuspicious detections:\n")
                                            suspiciousEngines.forEach {
                                                append(" - ${it.key}: ${it.value.result}\n")
                                            }
                                        }
                                    }
                                }
                                return@withTimeoutOrNull result
                            }
                            "queued" -> {
                                Log.d(TAG, "Scan is queued, waiting...")
                                delay(delayBetweenAttemptsMs)
                                continue
                            }
                            "in-progress" -> {
                                val engineResults = attributes?.results
                                val stats = attributes?.stats
                                val totalEngines = engineResults?.size ?: 0
                                val scannedEngines = engineResults?.count { it.value.result != null } ?: 0
                                val progress = if (totalEngines > 0) {
                                    (scannedEngines.toFloat() / totalEngines * 100).toInt()
                                } else 0

                                Log.d(TAG, "Scan in progress: $progress% ($scannedEngines/$totalEngines engines)")

                                // Nếu đã chờ quá 10 lần (50 giây), hiển thị kết quả hiện tại
                                if (attempts >= 10) {
                                    val result = buildString {
                                        append("Scan in progress (${progress}% complete).\n\n")
                                        append("Current Stats:\n")
                                        append(" - Malicious: ${stats?.malicious}\n")
                                        append(" - Suspicious: ${stats?.suspicious}\n")
                                        append(" - Harmless: ${stats?.harmless}\n")
                                        append(" - Undetected: ${stats?.undetected}\n")
                                        append(" - Timeout: ${stats?.timeout}\n")
                                        append(" - Total engines: $totalEngines\n")
                                        append(" - Scanned engines: $scannedEngines\n\n")
                                        append("Note: Some engines are still scanning. Results may be updated later.")
                                    }
                                    return@withTimeoutOrNull result
                                }

                                delay(delayBetweenAttemptsMs)
                                continue
                            }
                            else -> {
                                lastError = "Unexpected scan status: $status"
                                Log.e(TAG, lastError)
                                break
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        lastError = "Error getting scan report: $errorBody"
                        Log.e(TAG, lastError)
                        delay(delayBetweenAttemptsMs)
                    }
                } catch (e: Exception) {
                    lastError = "Exception while waiting for scan result: ${e.message}"
                    Log.e(TAG, lastError, e)
                    delay(delayBetweenAttemptsMs)
                }
            }

            return@withTimeoutOrNull lastError ?: "Scan did not complete in $maxAttempts attempts. Last status: $lastStatus"
        } ?: "Scan timed out after waiting ${totalTimeout / 1000} seconds."
    }
}