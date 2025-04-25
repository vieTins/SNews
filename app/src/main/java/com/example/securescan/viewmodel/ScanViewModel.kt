package com.example.securescan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.network.VirusTotalApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ScanViewModel : ViewModel() {
    private val apiKey = "4aa49aa105d9fe124cbfb0f0073ea91a6194a6738ef735d5e112e605b78edacb"
    private val TAG = "VirusTotalScanner"



    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.virustotal.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(VirusTotalApiService::class.java)

    fun scanFile(filePath: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            onResult("Starting file scan...")

            try {
                val file = File(filePath)
                if (!file.exists()) {
                    onResult("Error: File does not exist")
                    return@launch
                }

                val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
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
                    onResult(scanResult)
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Error uploading file. Code: $errorCode, Error: $errorBody")
                    onResult("Error uploading file. HTTP Status: $errorCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during file scan", e)
                onResult("Error: ${e.message ?: "Unknown error occurred"}")
            }
        }
    }

    fun scanUrl(url: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            onResult("Starting URL scan...")

            // Chuẩn hóa URL trước khi gửi
            val normalizedUrl = normalizeUrl(url)
            Log.d(TAG, "Normalized URL for scanning: $normalizedUrl")

            try {
                val response = apiService.scanUrl(apiKey, normalizedUrl)

                Log.d(TAG, "Response from URL scan: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val analysisId = response.body()?.data?.id ?: ""
                    Log.d(TAG, "URL Scan submitted. Analysis ID: ${response.body()?.data?.id}")
                    if (analysisId.isEmpty()) {
                        onResult("Error: Could not get analysis ID")
                        return@launch
                    }


                    val scanResult = waitForScanResult(analysisId)
                    onResult(scanResult)
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Error scanning URL: $normalizedUrl. Code: $errorCode, Error: $errorBody")
                    onResult("Error submitting URL. HTTP Status: $errorCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during URL scan: $normalizedUrl", e)
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
                Log.d(TAG, "Checking scan result, attempt $attempts of $maxAttempts")

                try {
                    val response = apiService.getScanReport(apiKey, analysisId)

                    if (response.isSuccessful && response.body() != null) {
                        val attributes = response.body()?.data?.attributes
                        val status = attributes?.status ?: "unknown"
                        lastStatus = status

                        Log.d(TAG, "Current scan status: $status")

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

                            Log.d(TAG, "Final scan result:\n$result")
                            return@withTimeoutOrNull result
                        } else {
                            Log.d(TAG, "Not enough engines scanned yet. Scanned: $scannedEngines/$totalEngines")
                            delay(delayBetweenAttemptsMs)
                            continue
                        }
                    } else {
                        Log.e(TAG, "Error fetching scan report. HTTP ${response.code()}")
                        delay(delayBetweenAttemptsMs)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception while checking scan result", e)
                    delay(delayBetweenAttemptsMs)
                }
            }

            return@withTimeoutOrNull "Scan did not complete in $maxAttempts attempts. Last status: $lastStatus"
        } ?: "Scan timed out after waiting ${totalTimeout / 1000} seconds."
    }

}