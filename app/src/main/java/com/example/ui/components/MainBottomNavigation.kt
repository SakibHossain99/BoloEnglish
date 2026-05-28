package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.ui.navigation.Routes
import com.example.ui.theme.PrimaryBlue

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("হোম", Icons.Default.Home, Routes.HOME),
    BottomNavItem("ফ্ল্যাশকার্ড", Icons.Default.List, Routes.FLASHCARDS),
    BottomNavItem("প্র্যাকটিস", Icons.Default.Person, Routes.PRACTICE),
    BottomNavItem("উন্নতি", Icons.Default.Star, Routes.PROGRESS),
    BottomNavItem("সেটিংস", Icons.Default.Settings, Routes.SETTINGS)
)

@Composable
fun MainBottomNavigation(navController: NavController, currentRoute: String?) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = com.example.ui.theme.TextSecondary,
                    unselectedTextColor = com.example.ui.theme.TextSecondary
                )
            )
        }
    }
}
