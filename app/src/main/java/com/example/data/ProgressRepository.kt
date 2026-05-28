package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class ProgressRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("boloEnglish_v1", Context.MODE_PRIVATE)
    
    private val _progress = MutableStateFlow(loadProgress())
    val progress: StateFlow<UserProgress> = _progress.asStateFlow()

    private val _flashcards = MutableStateFlow(loadFlashcards())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private fun loadProgress(): UserProgress {
        val jsonStr = prefs.getString("user_progress", null)
        return if (jsonStr != null) {
            try { Json.decodeFromString(jsonStr) } catch (e: Exception) { UserProgress() }
        } else {
            UserProgress()
        }
    }

    private fun loadFlashcards(): List<Flashcard> {
        val jsonStr = prefs.getString("flashcards", null)
        return if (jsonStr != null) {
            try { Json.decodeFromString(jsonStr) } catch (e: Exception) { emptyList() }
        } else {
            emptyList()
        }
    }

    private fun saveProgressLocal(p: UserProgress) {
        prefs.edit().putString("user_progress", Json.encodeToString(p)).apply()
        _progress.value = p
    }

    private fun saveFlashcardsLocal(f: List<Flashcard>) {
        prefs.edit().putString("flashcards", Json.encodeToString(f)).apply()
        _flashcards.value = f
    }

    fun updateProgress(updater: (UserProgress) -> UserProgress) {
        _progress.update { current ->
            val updated = updater(current)
            saveProgressLocal(updated)
            updated
        }
    }

    fun updateFlashcards(updater: (List<Flashcard>) -> List<Flashcard>) {
        _flashcards.update { current ->
            val updated = updater(current)
            saveFlashcardsLocal(updated)
            updated
        }
    }
    
    fun completeLesson(day: Int, score: Int, xp: Int) {
        updateProgress { p ->
            val today = LocalDate.now().toEpochDay()
            val newStreak = if (p.lastCompletedDateEpochDay == today) {
                p.streak
            } else if (p.lastCompletedDateEpochDay == today - 1) {
                p.streak + 1
            } else {
                1
            }
            
            val newCompleted = if (!p.completedDays.contains(day)) p.completedDays + day else p.completedDays
            p.copy(
                streak = newStreak,
                lastCompletedDateEpochDay = today,
                completedDays = newCompleted,
                xpTotal = p.xpTotal + xp,
                quizScores = p.quizScores + ("day_$day" to score),
                currentPhase = if (day in 1..30) 1 else if (day in 31..60) 2 else 3,
                currentDay = if (!p.completedDays.contains(day)) p.currentDay + 1 else p.currentDay
            )
        }
    }

    fun processFlashcardReview(wordId: String, quality: Int) {
        // quality: 1 = Hard, 3 = OK, 5 = Easy
        updateFlashcards { cards ->
            cards.map { card ->
                if (card.wordId == wordId) {
                    val newEase = maxOf(1.3f, card.easeFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)))
                    val newInterval = when (quality) {
                        1 -> 1
                        3 -> (card.interval * card.easeFactor).toInt().coerceAtLeast(1)
                        5 -> (card.interval * card.easeFactor * 1.3f).toInt().coerceAtLeast(1)
                        else -> 1
                    }
                    card.copy(
                        easeFactor = newEase,
                        interval = newInterval,
                        repetitions = card.repetitions + 1,
                        nextReviewEpochDay = LocalDate.now().toEpochDay() + newInterval
                    )
                } else card
            }
        }
    }

    fun resetProgress() {
        prefs.edit().clear().apply()
        _progress.value = UserProgress()
        _flashcards.value = emptyList()
    }
}
