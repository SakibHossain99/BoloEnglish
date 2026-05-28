package com.example.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.ProgressRepository
import com.example.ui.navigation.Routes
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessGreen

@Composable
fun OnboardingScreen(navController: NavController, repository: ProgressRepository) {
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("30 min") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (step) {
                1 -> {
                    Text("আপনার নাম কী?", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Enter your name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { if (name.isNotBlank()) step = 2 },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = name.isNotBlank()
                    ) {
                        Text("পরবর্তী (Next)")
                    }
                }
                2 -> {
                    Text("প্রতিদিন কতক্ষণ পড়বেন?", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    val options = listOf("30 min", "45 min", "60 min")
                    options.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            RadioButton(
                                selected = time == option,
                                onClick = { time = option }
                            )
                            Text(option, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { step = 3 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("পরবর্তী (Next)")
                    }
                }
                3 -> {
                    Text("আজ থেকেই শুরু!", style = MaterialTheme.typography.titleLarge, color = SuccessGreen)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            repository.updateProgress {
                                it.copy(userName = name)
                            }
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.ONBOARDING) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Dashboard-এ চলো")
                    }
                }
            }
        }
    }
}
