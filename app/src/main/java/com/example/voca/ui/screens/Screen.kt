package com.example.voca.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.voca.ui.theme.*
import com.example.voca.ui.viewmodel.WordViewModel

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    data object Home : Screen("home_tab", Icons.Default.Home, "Home")
    data object Progress : Screen("progress_tab", Icons.Default.BarChart, "Progress")
    data object Settings : Screen("settings_tab", Icons.Default.Settings, "Settings")
    data object WordDetail : Screen("word_detail/{wordId}", Icons.Default.Home, "Detail") {
        fun createRoute(wordId: String) = "word_detail/$wordId"
    }
    data object LearnedHistory : Screen("learned_history", Icons.Default.BarChart, "History")
}

@Composable
fun VocaMainApp(viewModel: WordViewModel) {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
    } else {
        Scaffold(
            bottomBar = {
                Surface(shadowElevation = 16.dp) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        val items = listOf(Screen.Home, Screen.Progress, Screen.Settings)

                        items.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(screen.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = VocaPinkDark,
                                    unselectedIconColor = VocaTextGrey,
                                    selectedTextColor = VocaPinkDark,
                                    unselectedTextColor = VocaTextGrey,
                                    indicatorColor = VocaPinkMain.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }
            },
            containerColor = VocaBg
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    WordListScreen(
                        viewModel = viewModel,
                        onWordClick = { wordId ->
                            navController.navigate(Screen.WordDetail.createRoute(wordId))
                        }
                    )
                }
                composable(Screen.Progress.route) {
                    ProgressScreen(
                        viewModel = viewModel,
                        onHistoryClick = {
                            navController.navigate(Screen.LearnedHistory.route)
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(viewModel = viewModel)
                }
                composable(Screen.WordDetail.route) { backStackEntry ->
                    val wordId = backStackEntry.arguments?.getString("wordId")
                    WordDetailScreen(
                        wordId = wordId,
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.LearnedHistory.route) {
                    LearnedWordsHistoryScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
