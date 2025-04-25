package com.example.securescan.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

object FirebaseSeeder {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun seedData() {
        seedUsers()
        seedScanHistory()
        seedNewsHistory()
        seedSecurityAlerts()
    }

    private fun seedUsers() {
        val users: List<Map<String, Any>> = listOf(
            mapOf(
                "fullName" to "Nguyễn Văn A",
                "email" to "nguyenvana@example.com",
                "phoneNumber" to "0987654321",
                "address" to "Hà Nội, Việt Nam",
                "registrationDate" to FieldValue.serverTimestamp(),
                "lastLoginDevice" to "Android"
            ),
            mapOf(
                "fullName" to "Trần Thị B",
                "email" to "tranthib@example.com",
                "phoneNumber" to "0977777888",
                "address" to "TP. Hồ Chí Minh, Việt Nam",
                "registrationDate" to FieldValue.serverTimestamp(),
                "lastLoginDevice" to "iPhone"
            )
        )

        users.forEachIndexed { index, user ->
            db.collection("users")
                .document("user_${index + 1}")
                .set(user)
                .addOnSuccessListener { Log.d("FirebaseSeeder", "User ${index + 1} added") }
                .addOnFailureListener { e: Exception -> Log.e("FirebaseSeeder", "Error adding user", e) }
        }
    }

    private fun seedScanHistory() {
        val scans: List<MutableMap<String, Any>> = listOf(
            mutableMapOf(
                "type" to "file",
                "scanTarget" to "document.pdf",
                "timestamp" to FieldValue.serverTimestamp(),
                "isSafe" to true,
                "threatLevel" to "low",
                "details" to mutableMapOf("engineResults" to listOf<String>(), "detectionRatio" to "0/60")
            ),
            mutableMapOf(
                "type" to "url",
                "scanTarget" to "https://malicious-site.com",
                "timestamp" to FieldValue.serverTimestamp(),
                "isSafe" to false,
                "threatLevel" to "high",
                "details" to mutableMapOf("engineResults" to listOf("VirusTotal: Detected"), "detectionRatio" to "45/60")
            )
        )

        scans.forEachIndexed { index, scan ->
            db.collection("scan_history")
                .document("user_1")
                .collection("scans")
                .document("scan_${index + 1}")
                .set(scan) // Đã fix lỗi
                .addOnSuccessListener { Log.d("FirebaseSeeder", "Scan ${index + 1} added") }
                .addOnFailureListener { e -> Log.e("FirebaseSeeder", "Error adding scan", e) }
        }
    }


    private fun seedNewsHistory() {
        val articles: List<Map<String, Any>> = listOf(
            mapOf(
                "articleTitle" to "Cảnh báo mã độc mới tấn công người dùng Android",
                "articleUrl" to "https://securitynews.com/article1",
                "readTimestamp" to FieldValue.serverTimestamp(),
                "category" to "Mobile Security"
            ),
            mapOf(
                "articleTitle" to "Hacker tấn công hệ thống ngân hàng lớn",
                "articleUrl" to "https://securitynews.com/article2",
                "readTimestamp" to FieldValue.serverTimestamp(),
                "category" to "Banking Security"
            )
        )

        articles.forEachIndexed { index, article ->
            db.collection("news_history")
                .document("user_1")
                .collection("articles")
                .document("news_${index + 1}")
                .set(article)
                .addOnSuccessListener { Log.d("FirebaseSeeder", "News ${index + 1} added") }
                .addOnFailureListener { e: Exception -> Log.e("FirebaseSeeder", "Error adding news", e) }
        }
    }

    private fun seedSecurityAlerts() {
        val alerts: List<Map<String, Any>> = listOf(
            mapOf(
                "title" to "Lỗ hổng bảo mật nghiêm trọng trong Windows",
                "description" to "Microsoft cảnh báo lỗ hổng có thể bị khai thác.",
                "severity" to "critical",
                "relatedLink" to "https://securitynews.com/alert1",
                "publishDate" to FieldValue.serverTimestamp(),
                "tags" to listOf("Windows", "Critical Vulnerability")
            ),
            mapOf(
                "title" to "Phát hiện ransomware mới tấn công doanh nghiệp",
                "description" to "Loại ransomware này có thể mã hóa toàn bộ dữ liệu công ty.",
                "severity" to "warning",
                "relatedLink" to "https://securitynews.com/alert2",
                "publishDate" to FieldValue.serverTimestamp(),
                "tags" to listOf("Ransomware", "Cyber Attack")
            )
        )

        alerts.forEachIndexed { index, alert ->
            db.collection("security_alerts")
                .document("alert_${index + 1}")
                .set(alert)
                .addOnSuccessListener { Log.d("FirebaseSeeder", "Alert ${index + 1} added") }
                .addOnFailureListener { e: Exception -> Log.e("FirebaseSeeder", "Error adding alert", e) }
        }
    }
}