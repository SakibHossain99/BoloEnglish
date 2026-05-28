package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@Serializable
data class Part(
    val text: String? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

object GeminiApiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val jsonFormatter = Json { ignoreUnknownKeys = true }

    suspend fun generateContent(apiKey: String, requestData: GenerateContentRequest): GenerateContentResponse = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val jsonPayload = jsonFormatter.encodeToString(requestData)
        val body = jsonPayload.toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
            
        val response = okHttpClient.newCall(request).execute()
        val responseBodyString = response.body?.string() ?: ""
        
        if (response.isSuccessful && responseBodyString.isNotEmpty()) {
            jsonFormatter.decodeFromString(responseBodyString)
        } else {
            GenerateContentResponse()
        }
    }
}
