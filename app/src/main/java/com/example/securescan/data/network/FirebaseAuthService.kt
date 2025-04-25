package com.example.securescan.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseAuthService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // hàm đăng nhập sử dụng Firebase Authentication
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }


    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Sau khi đăng ký thành công, tạo document trong Firestore
                        val userData = hashMapOf(
                            "city" to "",
                            "id" to it.uid,
                            "email" to it.email,
                            "name" to it.email,
                            "fcmToken" to "FCM_TOKEN",
                            "phone" to "" ,
                            "profilePic" to "",
                        )
                        db.collection("users").document(it.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                callback(true, null)
                            }
                            .addOnFailureListener { e ->
                                callback(false, "Đăng ký thành công nhưng lỗi khi lưu dữ liệu: ${e.message}")
                            }
                    } ?: callback(false, "Không lấy được user sau khi đăng ký.")
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }


    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun logout() {
        auth.signOut()
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                auth.signOut()
            }
        }
    }
}