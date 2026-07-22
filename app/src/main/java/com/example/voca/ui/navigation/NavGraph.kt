package com.example.voca.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.voca.ui.screens.WordDetailScreen
import com.example.voca.ui.screens.WordListScreen
import com.example.voca.ui.viewmodel.WordViewModel

sealed class Screen(val route: String) {
    object WordList : Screen("word_list")
    object WordDetail : Screen("word_detail/{wordId}") {
        fun createRoute(wordId: String) = "word_detail/$wordId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: WordViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.WordList.route
    ) {
        composable(Screen.WordList.route) {
            WordListScreen(
                viewModel = viewModel,
                onWordClick = { wordId ->
                    navController.navigate(Screen.WordDetail.createRoute(wordId))
                }
            )
        }
        composable(Screen.WordDetail.route) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId")
            WordDetailScreen(
                wordId = wordId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
