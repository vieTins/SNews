package com.example.securescan.data.network

import com.example.securescan.data.models.ScanResultResponse
import com.example.securescan.data.models.VirusTotalResponse
import okhttp3.MultipartBody
import retrofit2.*
import retrofit2.http.*

interface VirusTotalApiService {

    @Multipart
    @POST("files")
    suspend fun uploadFile(
        @Header("x-apikey") apiKey: String,
        @Part file: MultipartBody.Part
    ): Response<VirusTotalResponse>

    @FormUrlEncoded
    @POST("urls")
    suspend fun scanUrl(
        @Header("x-apikey") apiKey: String,
        @Field("url") url: String
    ): Response<VirusTotalResponse>

    @GET("analyses/{id}")
    suspend fun getScanReport(
        @Header("x-apikey") apiKey: String,
        @Path("id") analysisId: String
    ): Response<VirusTotalResponse>
}
