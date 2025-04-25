package com.example.securescan.data.repository

import com.example.securescan.data.ScanPhone
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class ScanPhoneCardRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getScanPhone(
        onSuccess: (List<ScanPhone>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("malicous_phone")
            .get()
            .addOnSuccessListener { result ->
                val scanPhoneCards = result.mapNotNull { it.toObject(ScanPhone::class.java) }
                onSuccess(scanPhoneCards)
            }
            .addOnFailureListener { onFailure(it) }
    }
    fun checkPhoneIsPhishing(
        phone: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val malicousQuery = db.collection("malicous_phone")
            .whereEqualTo("phone", phone)
            .get()

        val reportQuery = db.collection("reports")
            .whereEqualTo("type", "phone")
            .whereEqualTo("check", true)
            .whereEqualTo("value", phone)
            .get()

        Tasks.whenAllSuccess<QuerySnapshot>(malicousQuery, reportQuery)
            .addOnSuccessListener { results ->
                val malicousResult = results[0] as QuerySnapshot
                val reportResult = results[1] as QuerySnapshot

                // Nếu tồn tại ở 1 trong 2 nguồn => là phishing
                val isPhishing = !malicousResult.isEmpty || !reportResult.isEmpty
                onSuccess(isPhishing)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


}