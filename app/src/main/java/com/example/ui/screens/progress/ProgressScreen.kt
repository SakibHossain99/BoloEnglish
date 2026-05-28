package com.example.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.ProgressRepository
import com.example.ui.components.MainBottomNavigation
import com.example.ui.navigation.Routes
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navController: NavController, repository: ProgressRepository) {
    val progress by repository.progress.collectAsState()
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("তোমার Progress 📊") }) },
        bottomBar = { MainBottomNavigation(navController, Routes.PROGRESS) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Day ${progress.currentDay} / 90 — Phase ${progress.currentPhase}", style = MaterialTheme.typography.titleMedium)
            
            // Journey Ring
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = PrimaryBlue,
                        startAngle = -90f,
                        sweepAngle = (progress.currentDay.toFloat() / 90f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text("${progress.currentDay}", style = MaterialTheme.typography.displayLarge)
            }
            
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(label = "🔥 Streak", value = "${progress.streak} দিন")
                StatItem(label = "🏆 XP", value = "${progress.xpTotal}")
                StatItem(label = "📚 Lessons", value = "${progress.completedDays.size}")
            }
            
            Divider()
            
            // Skill Strength Radar Placeholder
            Text("💪 Skill Strength", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💚 সবচেয়ে ভালো: Reading (${progress.skills.reading}%)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🔶 আরও মনোযোগ দাও: Speaking (${progress.skills.speaking}%)", color = AccentOrange)
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
