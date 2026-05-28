package com.example.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.ProgressRepository
import com.example.ui.navigation.Routes
import kotlinx.coroutines.launch

enum class AuthMode {
    SIGN_IN, SIGN_UP, FORGOT_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailAuthScreen(navController: NavController, repository: ProgressRepository) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var showSuccessReset by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when (mode) {
                            AuthMode.SIGN_IN -> "Sign In"
                            AuthMode.SIGN_UP -> "Create Account"
                            AuthMode.FORGOT_PASSWORD -> "Reset Password"
                        }
                    ) 
                },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (showSuccessReset) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text("Password reset link sent to $email. Check your inbox.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMsg = "" },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (mode != AuthMode.FORGOT_PASSWORD) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMsg = "" },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (mode == AuthMode.SIGN_UP) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMsg = "" },
                    label = { Text("Confirm Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (email.isBlank()) {
                        errorMsg = "Email cannot be empty"
                        return@Button
                    }
                    when (mode) {
                        AuthMode.SIGN_UP -> {
                            if (password.length < 6) {
                                errorMsg = "Password must be at least 6 characters"
                                return@Button
                            }
                            if (password != confirmPassword) {
                                errorMsg = "Passwords do not match"
                                return@Button
                            }
                            com.google.firebase.auth.FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        repository.updateProgress { it.copy(authMethod = "Email") }
                                        navController.navigate(Routes.ONBOARDING)
                                    } else {
                                        errorMsg = task.exception?.message ?: "Registration failed"
                                    }
                                }
                        }
                        AuthMode.SIGN_IN -> {
                            com.google.firebase.auth.FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        repository.updateProgress { it.copy(authMethod = "Email") }
                                        navController.navigate(Routes.HOME) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    } else {
                                        errorMsg = task.exception?.message ?: "Invalid email or password"
                                    }
                                }
                        }
                        AuthMode.FORGOT_PASSWORD -> {
                            com.google.firebase.auth.FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        showSuccessReset = true
                                        mode = AuthMode.SIGN_IN
                                    } else {
                                        errorMsg = task.exception?.message ?: "Failed to send reset email"
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    when (mode) {
                        AuthMode.SIGN_IN -> "Login"
                        AuthMode.SIGN_UP -> "Create Account"
                        AuthMode.FORGOT_PASSWORD -> "Send Reset Link"
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (mode) {
                AuthMode.SIGN_IN -> {
                    TextButton(onClick = { mode = AuthMode.FORGOT_PASSWORD; showSuccessReset = false; errorMsg = "" }) {
                        Text("Forgot Password?")
                    }
                    TextButton(onClick = { mode = AuthMode.SIGN_UP; showSuccessReset = false; errorMsg = "" }) {
                        Text("Don't have an account? Sign Up")
                    }
                }
                AuthMode.SIGN_UP -> {
                    TextButton(onClick = { mode = AuthMode.SIGN_IN; showSuccessReset = false; errorMsg = "" }) {
                        Text("Already have an account? Sign In")
                    }
                }
                AuthMode.FORGOT_PASSWORD -> {
                    TextButton(onClick = { mode = AuthMode.SIGN_IN; showSuccessReset = false; errorMsg = "" }) {
                        Text("Back to Sign In")
                    }
                }
            }
        }
    }
}
