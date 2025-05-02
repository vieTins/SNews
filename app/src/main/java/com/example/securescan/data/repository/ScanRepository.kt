package com.example.securescan.data.repository

import com.example.securescan.data.models.ScanHistory
import com.example.securescan.data.models.ScanResult
import com.example.securescan.data.models.ScanType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScanRepository {
    private val db = FirebaseFirestore.getInstance()
    private val scanHistoryCollection = db.collection("scan_history")

    suspend fun saveScanHistory(scanHistory: ScanHistory) {
        try {
            scanHistoryCollection
                .add(scanHistory)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getScanHistoryByUserId(userId: String): List<ScanHistory> {
        return try {
            val snapshot = scanHistoryCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val history = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ScanHistory::class.java)
            }
            history
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun determineScanResult(type: ScanType, maliciousCount: Int): String {
        return when (type) {
            ScanType.PHONE, ScanType.BANK_ACCOUNT -> {
                if (maliciousCount > 0) ScanResult.FRAUD.name else ScanResult.NO_INFO.name
            }
            ScanType.WEBSITE -> {
                when {
                    maliciousCount > 5 -> ScanResult.DANGEROUS.name
                    maliciousCount > 0 -> ScanResult.SUSPICIOUS.name
                    else -> ScanResult.NO_INFO.name
                }
            }
            ScanType.FILE -> {
                if (maliciousCount > 0) ScanResult.DANGEROUS.name else ScanResult.NO_INFO.name
            }
        }
    }
}

