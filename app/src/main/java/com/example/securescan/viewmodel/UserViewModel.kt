package com.example.securescan.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.models.User
import com.example.securescan.data.network.CloudinaryUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class UserViewModel : ViewModel() {

    private val _user = mutableStateOf(User())
    val user: State<User> = _user

    val message = mutableStateOf("")
    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val uploadScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        fetchUserData()
    }

    override fun onCleared() {
        super.onCleared()
        uploadScope.cancel()
    }

    // Fetch user data from Firestore
    private fun fetchUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _user.value = doc.toObject(User::class.java) ?: User()
                }
            }
            .addOnFailureListener {
                Log.e("UserViewModel", "FetchUserData Failed: ${it.message}")
            }
    }

    // Update user data (including profile picture upload)
    fun updateUser(context: Context, updatedUser: User) {
        _isLoading.value = true
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            message.value = "Không xác định được người dùng"
            _isSuccess.value = false
            _isLoading.value = false
            return
        }

        val db = FirebaseFirestore.getInstance()
        val localUri = updatedUser.profilePic

        if (!localUri.isNullOrBlank() && (localUri.startsWith("content:") || localUri.startsWith("file:"))) {
            uploadImageAndSaveUser(context, uid, updatedUser, db)
        } else {
            saveUserToFirestore(uid, updatedUser, db)
        }
    }

    // Handle image upload and update Firestore data
    private fun uploadImageAndSaveUser(context: Context, uid: String, updatedUser: User, db: FirebaseFirestore) {
        viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
            try {
                val uploadedUrl = uploadImageToCloudinary(context, Uri.parse(updatedUser.profilePic))

                if (uploadedUrl != null) {
                    Log.d("UserViewModel", "Cloudinary upload successful, URL: $uploadedUrl")
                    val updatedUserWithProfilePic = updatedUser.copy(profilePic = uploadedUrl)
                    Log.d("UserViewModel", "Attempting to save user to Firestore: $updatedUserWithProfilePic")

                    try {
                        db.collection("users").document(uid)
                            .set(updatedUserWithProfilePic)
                            .await()

                        withContext(Dispatchers.Main) {
                            Log.d("UserViewModel", "Firestore save successful")
                            _user.value = updatedUserWithProfilePic
                            message.value = "Cập nhật thành công"
                            _isSuccess.value = true
                        }
                    } catch (e: Exception) {
                        Log.e("UserViewModel", "Firestore save error: ${e.message}", e)
                        withContext(Dispatchers.Main) {
                            message.value = "Lỗi khi lưu dữ liệu: ${e.message}"
                            _isSuccess.value = false
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        message.value = "Không thể lấy URL ảnh sau khi upload"
                        _isSuccess.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error in uploadImageAndSaveUser: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    message.value = "Lỗi khi cập nhật: ${e.message}"
                    _isSuccess.value = false
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    // Upload image to Cloudinary with retry logic
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

    // Save user data to Firestore
    private fun saveUserToFirestore(uid: String, user: User, db: FirebaseFirestore) {
        Log.d("UserViewModel", "Starting Firestore save operation for user: $uid")
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("UserViewModel", "Firestore save successful for user: $uid")
                _user.value = user
                message.value = "Cập nhật thành công"
                _isSuccess.value = true
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                Log.e("UserViewModel", "Firestore Save Error: ${e.message}", e)
                message.value = "Cập nhật thất bại: ${e.message}"
                _isSuccess.value = false
                _isLoading.value = false
            }
    }

    // Create URI for image storage in external storage
    fun createImageUri(context: Context): Uri {
        val contentResolver = context.contentResolver
        val imageName = "profile_${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: throw IllegalStateException("Không thể tạo URI ảnh")
    }
}
