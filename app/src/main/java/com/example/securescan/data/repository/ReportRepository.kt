package com.example.securescan.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.securescan.data.models.ReportItem
import com.example.securescan.data.network.CloudinaryUploader
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException


class ReportRepository {

    private val db = FirebaseFirestore.getInstance()
    private val reportsCollection = db.collection("reports")
    private suspend fun uploadImageToCloudinary(context: Context, imageUri: Uri): String? {
        var retryCount = 0
        val maxRetries = 3

        while (retryCount < maxRetries) {
            try {
                Log.d("UserViewModel", "Attempt ${retryCount + 1} to upload image to Cloudinary")
                val uploadedUrl = CloudinaryUploader.uploadImage(context, imageUri)

                if (uploadedUrl != null) {
                    Log.d("UserViewModel", "Successfully uploaded to Cloudinary: $uploadedUrl")
                    return uploadedUrl
                } else {
                    Log.e("UserViewModel", "Upload returned null URL")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Upload attempt ${retryCount + 1} failed: ${e.message}")
            }

            retryCount++
            if (retryCount < maxRetries) {
                try {
                    delay(1000L * retryCount)
                } catch (e: CancellationException) {
                    Log.e("UserViewModel", "Delay was cancelled, but continuing upload attempts")
                    // Continue with retry despite cancellation of delay
                }
            }
        }

        Log.e("UserViewModel", "All upload attempts failed")
        return null
    }
    suspend fun submitReport(
        context: Context,
        report: ReportItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val newReportRef = reportsCollection.document()
            val reportWithId = report.copy(id = newReportRef.id)

            val uploadedUrl = uploadImageToCloudinary(context, Uri.parse(report.imageUrl))
            if (uploadedUrl != null) {
                val updatedReport = reportWithId.copy(imageUrl = uploadedUrl)

                newReportRef.set(updatedReport)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            } else {
                onFailure(Exception("Failed to upload image"))
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
    fun getReportsByUserId(
        userId: String,
        onSuccess: (List<ReportItem>) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        db.collection("reports")
            .whereEqualTo("reportedBy", userId)
            .get()
            .addOnSuccessListener { result ->
                val reports = result.mapNotNull { it.toObject(ReportItem::class.java) }
                onSuccess(reports)
            }
            .addOnFailureListener { onFailure(it) }
    }
    fun getAllReports(
        onSuccess: (List<ReportItem>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("reports")
            .get()
            .addOnSuccessListener { result ->
                try {
                    val reports = result.mapNotNull { doc ->
                        val type = doc.getString("type") ?: ""
                        val target = doc.getString("target") ?: ""
                        val description = doc.getString("description") ?: ""
                        val reportedBy = doc.getString("reportedBy") ?: ""
                        val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        val isCheck = doc.getBoolean("check") ?: false
                        val id = doc.id

                        ReportItem(
                            type = type,
                            target = target,
                            description = description,
                            reportedBy = reportedBy,
                            timestamp = timestamp,
                            check = isCheck,
                            id = id

                        )
                    }
                    onSuccess(reports)
                } catch (e: Exception) {
                    onFailure(e)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun filterReportByType(
        type: String,
        onSuccess: (List<ReportItem>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("reports")
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { result ->
                val reports = result.mapNotNull { it.toObject(ReportItem::class.java) }
                onSuccess(reports)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getReportById(
        reportId: String,
        onSuccess: (ReportItem) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        reportsCollection.document(reportId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val report = document.toObject(ReportItem::class.java)
                    if (report != null) {
                        onSuccess(report)
                    } else {
                        onFailure(Exception("Không thể chuyển đổi dữ liệu báo cáo"))
                    }
                } else {
                    onFailure(Exception("Không tìm thấy báo cáo"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    
    fun getReportCountByTarget(target: String, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        reportsCollection
            .whereEqualTo("target", target)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.size())
            }
            .addOnFailureListener { e ->
                onFailure("Lỗi khi đếm số lượng báo cáo: ${e.message}")
            }
    }

    fun getTopFraudTargets(limit: Int = 5, onSuccess: (List<Pair<String, Int>>) -> Unit, onFailure: (String) -> Unit) {
        reportsCollection
            .get()
            .addOnSuccessListener { documents ->
                val targetCounts = mutableMapOf<String, Int>()
                
                // Count reports for each target
                documents.forEach { doc ->
                    val target = doc.getString("target") ?: return@forEach
                    targetCounts[target] = (targetCounts[target] ?: 0) + 1
                }
                
                // Sort by count and get top targets
                val topTargets = targetCounts.entries
                    .sortedByDescending { it.value }
                    .take(limit)
                    .map { it.key to it.value }
                
                onSuccess(topTargets)
            }
            .addOnFailureListener { e ->
                onFailure("Lỗi khi lấy top target: ${e.message}")
            }
    }
}