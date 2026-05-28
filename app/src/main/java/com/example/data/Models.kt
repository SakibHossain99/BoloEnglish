package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class Skills(
    val listening: Int = 0,
    val speaking: Int = 0,
    val reading: Int = 0,
    val writing: Int = 0
)

@Serializable
data class Flashcard(
    val wordId: String,
    val word: String,
    val bengaliMeaning: String,
    val exampleSentence: String,
    val imageUrl: String = "",
    val interval: Int = 1,
    val easeFactor: Float = 2.5f,
    val repetitions: Int = 0,
    val addedOnDay: Int = 1,
    val nextReviewEpochDay: Long = 0
)

@Serializable
data class UserProgress(
    val userName: String = "",
    val authMethod: String = "guest",
    val dailyReminderTime: String = "08:00 AM",
    val bengaliToggle: Boolean = true,
    
    val currentDay: Int = 1,
    val currentPhase: Int = 1,
    val streak: Int = 0,
    val lastCompletedDateEpochDay: Long = 0,
    val completedDays: List<Int> = emptyList(),
    val xpTotal: Int = 0,
    val skills: Skills = Skills(),
    
    val quizScores: Map<String, Int> = emptyMap(),
    val studyTimeMinutes: Map<String, Int> = emptyMap()
)
