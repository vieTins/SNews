package com.example.securescan.data.repository

import com.example.securescan.data.models.ScanHistory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScanHistoryRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getScanHistory(userId: String): List<ScanHistory> {
        return try {
            val snapshot = db.collection("scan_history")
                .document(userId)
                .collection("scans")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(ScanHistory::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
