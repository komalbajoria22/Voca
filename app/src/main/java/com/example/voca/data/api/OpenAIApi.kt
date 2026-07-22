package com.example.voca.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIApi {
    @POST("v1/chat/completions")
    suspend fun getCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: OpenAIRequest
    ): OpenAIResponse
}
