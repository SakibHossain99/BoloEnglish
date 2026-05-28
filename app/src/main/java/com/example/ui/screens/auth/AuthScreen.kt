package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.ProgressRepository
import com.example.ui.navigation.Routes
import com.example.ui.theme.PrimaryBlue

@Composable
fun AuthScreen(navController: NavController, repository: ProgressRepository) {
    val progress by repository.progress.collectAsState()
    
    // Auto-redirect if already logged in and onboarding complete
    if (progress.userName.isNotEmpty() && progress.currentDay > 0) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.AUTH) { inclusive = true }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background Decorative Blobs
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Glassmorphism Card Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 450.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo Title
                        Text(
                            text = "BoloEnglish",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "“ইংরেজি শিখুন, বিশ্ব জয় করুন”",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                        )
                        
                        Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(bottom = 24.dp))
                        
                        // Google Auth Button
                        OutlinedButton(
                            onClick = {
                                // Mapped Firebase Google Sign-In hook
                                val dummyGoogleToken = "google_token_abc123"
                                val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(dummyGoogleToken, null)
                                com.google.firebase.auth.FirebaseAuth.getInstance().signInWithCredential(credential)
                                    .addOnCompleteListener { task ->
                                        // For demo/simulated run, update progress and navigate
                                        repository.updateProgress { it.copy(authMethod = "Google") }
                                        navController.navigate(Routes.ONBOARDING)
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                "Google-এর সাথে চালিয়ে যান",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Email/Password Auth Button
                        OutlinedButton(
                            onClick = {
                                navController.navigate(Routes.EMAIL_AUTH)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ইমেইল ও পাসওয়ার্ড দিয়ে এগিয়ে যান",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Anonymous Auth (Guest Button)
                        TextButton(
                            onClick = {
                                // Mapped Firebase Anonymous login hook
                                com.google.firebase.auth.FirebaseAuth.getInstance().signInAnonymously()
                                    .addOnCompleteListener { task ->
                                        repository.updateProgress { it.copy(authMethod = "anonymous") }
                                        navController.navigate(Routes.ONBOARDING)
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                "অতিথি হিসেবে চালিয়ে যান",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Terms
                        Text(
                            text = "এগিয়ে যাওয়ার মাধ্যমে, আপনি আমাদের শর্তাবলী মেনে নিচ্ছেন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
