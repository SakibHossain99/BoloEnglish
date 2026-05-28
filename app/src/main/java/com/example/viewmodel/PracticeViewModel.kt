package com.example.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.Content
import com.example.data.GenerateContentRequest
import com.example.data.Part
import com.example.data.GeminiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(val text: String, val isUser: Boolean)

class PracticeViewModel : ViewModel() {
    val chatHistory = mutableStateListOf<ChatMessage>()
    
    private var systemInstructionText: String = ""

    fun initChat(userName: String, day: Int) {
        systemInstructionText = """
            You are "Bolo Mentor", a warm and encouraging English teacher specifically trained for Bengali native speakers learning English from zero.
            Student Profile:
            - Name: $userName
            - Current Day: $day out of 90
            
            Your behavior rules:
            1. Matches student level.
            2. Keep responses short (1-2 sentences).
            3. Address Bengali specific errors.
            4. Speak English and occasional Bengali if necessary.
        """.trimIndent()
        
        chatHistory.add(ChatMessage("Hello $userName! 👋 Welcome to your practice session. How are you today?", false))
    }

    fun sendMessage(text: String) {
        chatHistory.add(ChatMessage(text, true))
        
        viewModelScope.launch {
            val response = try {
                makeGeminiRequest(text)
            } catch (e: Exception) {
                "Sorry, I am having trouble connecting right now."
            }
            chatHistory.add(ChatMessage(response, false))
        }
    }

    private suspend fun makeGeminiRequest(prompt: String): String = withContext(Dispatchers.IO) {
        // Handle mock or missing config
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        if (apiKey.isEmpty() || apiKey.startsWith("MY_GEMINI")) {
            return@withContext "⚠️ API Key not configured. Please add it to your Secrets panel."
        }

        // Build history conversation format
        val historicalContents = chatHistory.dropLast(1).map { msg ->
            Content(
                parts = listOf(Part(text = msg.text)),
                role = if (msg.isUser) "user" else "model"
            )
        }
        
        val contents = historicalContents + Content(parts = listOf(Part(text = prompt)), role = "user")

        val request = GenerateContentRequest(
            contents = contents,
            systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)), role = "model")
        )
        
        val response = GeminiApiService.generateContent(apiKey, request)
        response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response text"
    }
}
