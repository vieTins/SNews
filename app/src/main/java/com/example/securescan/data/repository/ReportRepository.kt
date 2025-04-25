package com.example.securescan.data.repository

import com.example.securescan.data.models.ReportItem
import com.google.firebase.firestore.FirebaseFirestore

class ReportRepository {

    private val db = FirebaseFirestore.getInstance()

    fun submitReport(
        report: ReportItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("reports")
            .add(report)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
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

                        ReportItem(
                            type = type,
                            target = target,
                            description = description,
                            reportedBy = reportedBy,
                            timestamp = timestamp,
                            check = isCheck
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
}