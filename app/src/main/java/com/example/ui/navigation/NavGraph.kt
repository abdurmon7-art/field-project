package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CompatibilityScreen
import com.example.ui.screens.GameModeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VoiceChatScreen
import com.example.ui.screens.VoiceEffectsScreen
import com.example.ui.screens.VoiceRecorderScreen
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.viewmodel.VoiceChangerViewModel

@Composable
fun AppNavGraph(viewModel: VoiceChangerViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = Screen.bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = CyberSurface,
                    contentColor = Color.White,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                screen.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) CyberCyan else Color.Gray
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CyberCyan else Color.Gray
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = CyberCardBg
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen(
                        onSplashFinished = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToEffects = { navController.navigate(Screen.VoiceEffects.route) },
                        onNavigateToGameMode = { navController.navigate(Screen.GameMode.route) },
                        onNavigateToVoiceChat = { navController.navigate(Screen.VoiceChat.route) },
                        onNavigateToRecorder = { navController.navigate(Screen.VoiceRecorder.route) },
                        onOpenSettings = { navController.navigate(Screen.Settings.route) },
                        onOpenPermissions = { navController.navigate(Screen.Permissions.route) },
                        onOpenDiagnostic = { navController.navigate(Screen.Compatibility.route) }
                    )
                }

                composable(Screen.VoiceEffects.route) {
                    VoiceEffectsScreen(
                        viewModel = viewModel,
                        onOpenSettings = { navController.navigate(Screen.Settings.route) }
                    )
                }

                composable(Screen.GameMode.route) {
                    GameModeScreen(
                        viewModel = viewModel,
                        onOpenSettings = { navController.navigate(Screen.Settings.route) },
                        onOpenDiagnostic = { navController.navigate(Screen.Compatibility.route) }
                    )
                }

                composable(Screen.VoiceChat.route) {
                    VoiceChatScreen(
                        viewModel = viewModel,
                        onOpenSettings = { navController.navigate(Screen.Settings.route) },
                        onNavigateToRecorder = { navController.navigate(Screen.VoiceRecorder.route) }
                    )
                }

                composable(Screen.VoiceRecorder.route) {
                    VoiceRecorderScreen(
                        viewModel = viewModel,
                        onOpenSettings = { navController.navigate(Screen.Settings.route) }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Permissions.route) {
                    PermissionsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Compatibility.route) {
                    CompatibilityScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
