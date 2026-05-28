package com.example.ui.screens.lesson

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.ProgressRepository
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(day: Int, navController: NavController, repository: ProgressRepository) {
    var currentStep by remember { mutableStateOf(1) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Day $day Lesson") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Stepper
            LinearProgressIndicator(
                progress = { currentStep / 5f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            // Step Content
            when(currentStep) {
                1 -> ListenAndRepeatStep { currentStep++ }
                2 -> SVOPuzzleStep { currentStep++ }
                3 -> VocabularyStep { currentStep++ }
                4 -> TenseSliderStep { currentStep++ }
                5 -> QuizStep { score ->
                    repository.completeLesson(day, score, xp = 100)
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun ListenAndRepeatStep(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("STEP 1 — 🎧 LISTEN & REPEAT", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("I am from Bangladesh", style = MaterialTheme.typography.headlineMedium)
        Text("Native speaker-এর মতো করে বলার চেষ্টা করো", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNext) { Text("Record (Mocked) & Continue") }
    }
}

@Composable
fun SVOPuzzleStep(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("STEP 2 — 📖 SVO PUZZLE", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("আমি চা পছন্দ করি", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        // Mock puzzle
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}) { Text("[I]") }
            Button(onClick = {}) { Text("[like]") }
            Button(onClick = {}) { Text("[tea]") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("English-এ Verb সবসময় Subject-এর পরে আসে!", color = SuccessGreen)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNext) { Text("Next") }
    }
}

@Composable
fun VocabularyStep(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("STEP 3 — 🧠 VOCABULARY", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Water", style = MaterialTheme.typography.headlineMedium)
                Text("Meaning: পানি")
                Text("I drink water.")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNext) { Text("Add to Flashcards & Next") }
    }
}

@Composable
fun TenseSliderStep(onNext: () -> Unit) {
    var tense by remember { mutableStateOf(1f) }
    val sentence = when (tense.toInt()) {
        0 -> "I ate rice"
        1 -> "I eat rice"
        else -> "I will eat rice"
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("STEP 4 — ⏱️ TENSE SLIDER", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(sentence, style = MaterialTheme.typography.headlineMedium)
        Slider(
            value = tense,
            onValueChange = { tense = it },
            valueRange = 0f..2f,
            steps = 1
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Yesterday")
            Text("Today")
            Text("Tomorrow")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNext) { Text("Next") }
    }
}

@Composable
fun QuizStep(onFinish: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("STEP 5 — ✅ QUIZ", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("How do you say 'আমি পানি পান করি'?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { onFinish(0) }, modifier = Modifier.fillMaxWidth()) { Text("I water drink") }
        OutlinedButton(onClick = { onFinish(100) }, modifier = Modifier.fillMaxWidth()) { Text("I drink water") }
    }
}
