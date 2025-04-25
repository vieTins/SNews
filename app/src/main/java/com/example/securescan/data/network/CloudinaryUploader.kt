package com.example.securescan.data.network

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object CloudinaryUploader {

    private const val CLOUD_NAME = "dfuorp3lb"
    private const val UPLOAD_PRESET = "ImgSNews"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun uploadImage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        Log.d("CloudinaryUploader", "Bắt đầu upload Uri: $uri")

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Không thể mở ảnh từ uri: $uri")

        val fileBytes = inputStream.use { it.readBytes() }
        val requestBody = fileBytes.toRequestBody("image/*".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "avatar.jpg", requestBody)
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
            .post(multipartBody)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            Log.d("CloudinaryUploader", "Cloudinary response: $responseBody")

            if (!response.isSuccessful || responseBody == null) {
                throw IOException("Upload thất bại. Code: ${response.code}, body: $responseBody")
            }

            val json = JSONObject(responseBody)
            val secureUrl = json.getString("secure_url")

            Log.d("CloudinaryUploader", "Upload thành công: $secureUrl")
            return@withContext secureUrl
        }
    }
}
