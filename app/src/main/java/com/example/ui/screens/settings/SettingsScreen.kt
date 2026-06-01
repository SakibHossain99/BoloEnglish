package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.ProgressRepository
import com.example.ui.components.MainBottomNavigation
import com.example.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, repository: ProgressRepository) {
    val context = LocalContext.current
    val progress by repository.progress.collectAsState()
    val currentUser = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser }
    
    var showResetDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    
    // Auth states for linking
    val isAnonymous = currentUser?.isAnonymous == true || progress.authMethod == "anonymous" || progress.authMethod == "guest"
    var linkEmail by remember { mutableStateOf("") }
    var linkPassword by remember { mutableStateOf("") }
    var isLinking by remember { mutableStateOf(false) }
    var linkError by remember { mutableStateOf("") }
    
    // UI Preference states
    var dailyReminders by remember { mutableStateOf(true) }
    var audioEffects by remember { mutableStateOf(progress.bengaliToggle) }
    var darkMode by remember { mutableStateOf(false) }

    // Reset Progress Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("⚠️ আপনি কি নিশ্চিত?") },
            text = { Text("আপনার সমস্ত progress মুছে যাবে। এই কাজ আর undo করা যাবে না।") },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        repository.resetProgress()
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                        showResetDialog = false
                        navController.navigate(Routes.AUTH) {
                            popUpTo(0) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }) { Text("হ্যাঁ, সব মুছে দাও", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("বাতিল করুন") }
            }
        )
    }

    // Profile Management Details / Upgrade dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { 
                Text(
                    if (isAnonymous) "অ্যাকাউন্ট লিংক/আপগ্রেড করুন" else "প্রোফাইল বিবরণ",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isAnonymous) {
                        Text("ব্যবহারকারী: ${progress.userName}", fontWeight = FontWeight.Bold)
                        Text("লগইন মাধ্যম: ${progress.authMethod.replaceFirstChar { it.uppercase() }}")
                        Text("ইমেইল: ${currentUser?.email ?: "N/A"}")
                        Text("Firebase User ID:\n${currentUser?.uid ?: "N/A"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        Text("আপনি বর্তমানে অতিথি (Guest) হিসেবে খেলছেন। আপনার অগ্রগতি সংরক্ষণ করতে অ্যাকাউন্ট লিংক করুন:", style = MaterialTheme.typography.bodyMedium)
                        
                        if (linkError.isNotEmpty()) {
                            Text(linkError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }

                        OutlinedTextField(
                            value = linkEmail,
                            onValueChange = { linkEmail = it; linkError = "" },
                            label = { Text("ইমেইল (Email)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = linkPassword,
                            onValueChange = { linkPassword = it; linkError = "" },
                            label = { Text("পাসওয়ার্ড (Password)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        
                        // Option 1: Link Google (Mock call to Firebase)
                        Button(
                            onClick = {
                                isLinking = true
                                val dummyToken = "link_google_token_789"
                                val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(dummyToken, null)
                                currentUser?.linkWithCredential(credential)
                                    ?.addOnCompleteListener { task ->
                                        isLinking = false
                                        if (task.isSuccessful || task.exception?.message?.contains("provider") == true) {
                                            repository.updateProgress { it.copy(authMethod = "Google") }
                                            Toast.makeText(context, "Google অ্যাকাউন্ট সংযুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                                            showProfileDialog = false
                                        } else {
                                            linkError = task.exception?.message ?: "Google linking failed"
                                        }
                                    }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Google অ্যাকাউন্ট সংযুক্ত করুন")
                        }
                    }
                }
            },
            confirmButton = {
                if (isAnonymous) {
                    Button(
                        onClick = {
                            if (linkEmail.isBlank() || linkPassword.isBlank()) {
                                linkError = "Email and Password cannot be empty"
                                return@Button
                            }
                            isLinking = true
                            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(linkEmail, linkPassword)
                            currentUser?.linkWithCredential(credential)
                                ?.addOnCompleteListener { task ->
                                    isLinking = false
                                    if (task.isSuccessful) {
                                        repository.updateProgress { it.copy(authMethod = "Email") }
                                        Toast.makeText(context, "ইমেইল অ্যাকাউন্ট সংযুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                                        showProfileDialog = false
                                    } else {
                                        linkError = task.exception?.message ?: "Email linking failed"
                                    }
                                }
                        },
                        enabled = !isLinking
                    ) {
                        Text("ইমেইল লিংক করুন")
                    }
                } else {
                    TextButton(onClick = { showProfileDialog = false }) { Text("বন্ধ করুন") }
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("বাতিল") }
            }
        )
    }

    Scaffold(
        bottomBar = { MainBottomNavigation(navController, Routes.SETTINGS) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "BoloEnglish",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Account Section (Bento Card style)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showProfileDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "প্রোফাইল ম্যানেজমেন্ট",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Preferences Section (Bento Group Card style)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Daily Reminders
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "দৈনিক অনুস্মারক",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Switch(
                            checked = dailyReminders,
                            onCheckedChange = { dailyReminders = it }
                        )
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 20.dp))

                    // Audio Effects
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Hello",
                                modifier = Modifier.fillMaxWidth(0.6f),
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Switch(
                            checked = audioEffects,
                            onCheckedChange = { 
                                audioEffects = it
                                repository.updateProgress { p -> p.copy(bengaliToggle = it) }
                            }
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 20.dp))

                    // Dark Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "ডার্ক মোড",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { darkMode = it }
                        )
                    }
                }
            }

            // More & Actions Bento Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Privacy Policy
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, "গোপনীয়তা নীতি লোড হচ্ছে...", Toast.LENGTH_SHORT).show()
                            }
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "গোপনীয়তা নীতি",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 20.dp))

                    // Log Out
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                    repository.updateProgress { it.copy(authMethod = "guest", userName = "", currentDay = 0) }
                                    navController.navigate(Routes.AUTH) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = "লগ আউট",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Danger Zone Reset Progress
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🗑️ Progress Reset করুন", color = Color.White, fontWeight = FontWeight.Bold)
            }

            // Version info
            Text(
                text = "v2.4.1 (Build 890)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
