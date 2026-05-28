package com.example.ui.screens.flashcards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.ProgressRepository
import com.example.ui.components.MainBottomNavigation
import com.example.ui.navigation.Routes
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(navController: NavController, repository: ProgressRepository) {
    val flashcards by repository.flashcards.collectAsState()
    
    // Using mocked cards if empty
    val displayCards = if (flashcards.isEmpty()) {
        listOf(
            com.example.data.Flashcard("1", "Hello", "নমস্কার/হ্যালো", "Hello, how are you?"),
            com.example.data.Flashcard("2", "Water", "পানি/জল", "I drink water every day.")
        )
    } else flashcards

    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Flashcards") }) },
        bottomBar = { MainBottomNavigation(navController, Routes.FLASHCARDS) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Review Due Today: ${displayCards.size}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(32.dp))
            
            if (currentIndex < displayCards.size) {
                val card = displayCards[currentIndex]
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable { isFlipped = !isFlipped },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (!isFlipped) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(card.word, style = MaterialTheme.typography.displayLarge)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(card.exampleSentence, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(card.bengaliMeaning, style = MaterialTheme.typography.displayLarge, color = PrimaryBlue)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(card.exampleSentence, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (isFlipped) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { repository.processFlashcardReview(card.wordId, 1); currentIndex++; isFlipped = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("😫 Hard") }
                        Button(onClick = { repository.processFlashcardReview(card.wordId, 3); currentIndex++; isFlipped = false }) { Text("😐 OK") }
                        Button(onClick = { repository.processFlashcardReview(card.wordId, 5); currentIndex++; isFlipped = false }, colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.SuccessGreen)) { Text("😊 Easy") }
                    }
                }
            } else {
                Text("🎉 আজকের review শেষ!", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
