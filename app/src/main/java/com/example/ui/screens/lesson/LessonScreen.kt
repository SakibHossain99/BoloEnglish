package com.example.ui.screens.lesson

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.*
import com.example.ui.theme.SuccessGreen

@Composable
fun rememberTextToSpeech(): TextToSpeech? {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    
    DisposableEffect(context) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.US
                tts = ttsInstance
            }
        }
        
        onDispose {
            try {
                ttsInstance?.stop()
                ttsInstance?.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return tts
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(day: Int, navController: NavController, repository: ProgressRepository) {
    var currentStep by remember { mutableStateOf(1) }
    val tts = rememberTextToSpeech()
    val lessonContent = remember(day) { Curriculum.getLessonContent(day) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lessonContent.title) },
                navigationIcon = {
                    IconButton(onClick = {
                        try {
                            navController.popBackStack()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }) {
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
                1 -> ListenAndRepeatStep(lessonContent.listenRepeatSentences, tts) { currentStep++ }
                2 -> SVOPuzzleStep(lessonContent.svoPuzzle, tts) { currentStep++ }
                3 -> VocabularyStep(lessonContent.vocabulary, repository, tts) { currentStep++ }
                4 -> TenseSliderStep(lessonContent.tenseSlider, tts) { currentStep++ }
                5 -> QuizStep(lessonContent.quiz, tts) { score ->
                    try {
                        repository.completeLesson(day, score, xp = 100)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        navController.popBackStack()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}

@Composable
fun PronunciationButton(text: String, tts: TextToSpeech?) {
    IconButton(
        onClick = {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        modifier = Modifier.size(36.dp),
        enabled = tts != null
    ) {
        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Pronounce",
            tint = if (tts != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ListenAndRepeatStep(sentences: List<SentenceItem>, tts: TextToSpeech?, onNext: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("STEP 1 — 🎧 LISTEN & REPEAT", style = MaterialTheme.typography.labelLarge)
        Text("নিচের বাক্যগুলো শুনুন এবং রিপ্রোডিউস করার চেষ্টা করুন", style = MaterialTheme.typography.bodyMedium)
        
        sentences.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.english, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(item.bengali, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    PronunciationButton(text = item.english, tts = tts)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { 
            Text("পরবর্তী ধাপ (Next)") 
        }
    }
}

@Composable
fun SVOPuzzleStep(puzzle: SvoPuzzleItem, tts: TextToSpeech?, onNext: () -> Unit) {
    val correctWords = puzzle.englishWords
    val scrambledWords = remember(puzzle) { puzzle.scrambledWords.toMutableStateList() }
    val selectedWords = remember(puzzle) { mutableStateListOf<String>() }
    val isCorrect = selectedWords.toList() == correctWords

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("STEP 2 — 📖 SVO PUZZLE", style = MaterialTheme.typography.labelLarge)
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Bengali Sentence:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                Text(puzzle.bengali, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }

        // Selected words slot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedWords.isEmpty()) {
                Text("নিচের শব্দগুলো ট্যাপ করে বাক্য গঠন করুন", color = MaterialTheme.colorScheme.outline)
            } else {
                selectedWords.forEach { word ->
                    Button(
                        onClick = {
                            selectedWords.remove(word)
                            scrambledWords.add(word)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(word, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }

        // Scrambled words pool
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            scrambledWords.forEach { word ->
                OutlinedButton(
                    onClick = {
                        scrambledWords.remove(word)
                        selectedWords.add(word)
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(word)
                }
            }
        }

        if (isCorrect) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("✅ চমৎকার! বাক্যটি সঠিক হয়েছে।", color = SuccessGreen, fontWeight = FontWeight.Bold)
                    PronunciationButton(text = correctWords.joinToString(" "), tts = tts)
                }
                Text(puzzle.tip, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNext,
            enabled = isCorrect,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("পরবর্তী ধাপ (Next)")
        }
    }
}

@Composable
fun VocabularyStep(vocabItems: List<VocabularyItem>, repository: ProgressRepository, tts: TextToSpeech?, onNext: () -> Unit) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("STEP 3 — 🧠 VOCABULARY", style = MaterialTheme.typography.labelLarge)
        
        vocabItems.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.word, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("অর্থ: ${item.meaning}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                        }
                        PronunciationButton(text = item.word, tts = tts)
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Example: ${item.example}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text("অনুবাদ: ${item.exampleMeaning}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        PronunciationButton(text = item.example, tts = tts)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                try {
                    repository.updateFlashcards { current ->
                        val newCards = vocabItems.map { item ->
                            com.example.data.Flashcard(
                                wordId = java.util.UUID.randomUUID().toString(),
                                word = item.word,
                                bengaliMeaning = item.meaning,
                                exampleSentence = item.example
                            )
                        }
                        current + newCards
                    }
                    Toast.makeText(context, "Added to Flashcards!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                onNext()
            },
            modifier = Modifier.fillMaxWidth()
        ) { 
            Text("Add to Flashcards & Next") 
        }
    }
}

@Composable
fun TenseSliderStep(tenseItem: TenseSliderItem, tts: TextToSpeech?, onNext: () -> Unit) {
    var tense by remember { mutableStateOf(1f) }
    val currentSentence = when (tense.toInt()) {
        0 -> tenseItem.past
        1 -> tenseItem.present
        else -> tenseItem.future
    }
    val currentMeaning = when (tense.toInt()) {
        0 -> tenseItem.pastMeaning
        1 -> tenseItem.presentMeaning
        else -> tenseItem.futureMeaning
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("STEP 4 — ⏱️ TENSE SLIDER", style = MaterialTheme.typography.labelLarge)
        
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(currentSentence, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    PronunciationButton(text = currentSentence, tts = tts)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(currentMeaning, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
        
        Slider(
            value = tense,
            onValueChange = { tense = it },
            valueRange = 0f..2f,
            steps = 1
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Yesterday (Past)", fontWeight = if (tense.toInt() == 0) FontWeight.Bold else FontWeight.Normal, color = if (tense.toInt() == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            Text("Today (Present)", fontWeight = if (tense.toInt() == 1) FontWeight.Bold else FontWeight.Normal, color = if (tense.toInt() == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            Text("Tomorrow (Future)", fontWeight = if (tense.toInt() == 2) FontWeight.Bold else FontWeight.Normal, color = if (tense.toInt() == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { 
            Text("পরবর্তী ধাপ (Next)") 
        }
    }
}

@Composable
fun QuizStep(quiz: QuizItem, tts: TextToSpeech?, onFinish: (Int) -> Unit) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("STEP 5 — ✅ QUIZ", style = MaterialTheme.typography.labelLarge)
        
        Text(quiz.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        quiz.options.forEachIndexed { index, option ->
            val buttonColor = if (selectedOption == index) {
                if (index == quiz.correctIndex) {
                    ButtonDefaults.outlinedButtonColors(containerColor = SuccessGreen.copy(alpha = 0.15f))
                } else {
                    ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                }
            } else {
                ButtonDefaults.outlinedButtonColors()
            }
            
            val borderColor = if (selectedOption == index) {
                if (index == quiz.correctIndex) SuccessGreen else MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }

            OutlinedButton(
                onClick = {
                    selectedOption = index
                    if (index == quiz.correctIndex) {
                        try {
                            tts?.speak(option, TextToSpeech.QUEUE_FLUSH, null, null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = buttonColor,
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selectedOption == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedOption == index) {
                            if (index == quiz.correctIndex) SuccessGreen else MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    if (selectedOption == index) {
                        Text(
                            text = if (index == quiz.correctIndex) "✅ সঠিক" else "❌ ভুল",
                            fontWeight = FontWeight.Bold,
                            color = if (index == quiz.correctIndex) SuccessGreen else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                val score = if (selectedOption == quiz.correctIndex) 100 else 0
                onFinish(score)
            },
            enabled = selectedOption != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("সম্পন্ন করুন (Finish)")
        }
    }
}
