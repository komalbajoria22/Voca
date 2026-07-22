package com.example.voca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voca.ui.screens.VocaMainApp
import com.example.voca.ui.theme.VocaTheme
import com.example.voca.ui.viewmodel.WordViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VocaTheme {
                val viewModel: WordViewModel = viewModel(factory = WordViewModel.Factory)
                VocaMainApp(viewModel = viewModel)
            }
        }
    }
}
