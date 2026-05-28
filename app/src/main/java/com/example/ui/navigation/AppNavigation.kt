package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.ProgressRepository
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.auth.OnboardingScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.lesson.LessonScreen
import com.example.ui.screens.flashcards.FlashcardsScreen
import com.example.ui.screens.practice.PracticeScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.screens.settings.SettingsScreen

object Routes {
    const val AUTH = "auth"
    const val EMAIL_AUTH = "email_auth"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val LESSON = "lesson/{day}"
    const val FLASHCARDS = "flashcards"
    const val PRACTICE = "practice"
    const val PROGRESS = "progress"
    const val SETTINGS = "settings"
    
    fun lessonRoute(day: Int) = "lesson/$day"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = remember { ProgressRepository(context) }
    
    NavHost(navController = navController, startDestination = Routes.AUTH) {
        composable(Routes.AUTH) {
            AuthScreen(
                navController = navController,
                repository = repository
            )
        }
        composable(Routes.EMAIL_AUTH) {
            com.example.ui.screens.auth.EmailAuthScreen(
                navController = navController,
                repository = repository
            )
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                navController = navController,
                repository = repository
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                repository = repository
            )
        }
        composable(Routes.LESSON) { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day")?.toIntOrNull() ?: 1
            LessonScreen(
                day = day,
                navController = navController,
                repository = repository
            )
        }
        composable(Routes.FLASHCARDS) {
            FlashcardsScreen(
                navController = navController,
                repository = repository
            )
        }
        composable(Routes.PRACTICE) {
            PracticeScreen(
                navController = navController,
                repository = repository
            )
        }
        composable(Routes.PROGRESS) {
            ProgressScreen(
                navController = navController,
                repository = repository
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                navController = navController,
                repository = repository
            )
        }
    }
}
