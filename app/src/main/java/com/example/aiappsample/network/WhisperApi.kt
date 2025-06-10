package com.example.aiappsample.network

import com.example.aiappsample.network.model.TextGenerateModel
import com.example.aiappsample.network.model.WhisperModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WhisperApi {
    @Multipart
    @POST("v1/inference/distil-whisper/distil-large-v3")
    suspend fun postWhisper(@Part audio: MultipartBody.Part): Response<WhisperModel>

    @POST("v1/inference/mistralai/Mistral-7B-Instruct-v0.3")
    suspend fun generateAnswer(@Body body: RequestBody): Response<TextGenerateModel>
}