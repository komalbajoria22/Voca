package com.example.voca.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.ui.theme.VocaPinkMain
import com.example.voca.ui.theme.VocaTextDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VocaPinkMain),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Voca",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = VocaTextDark
            )
        }
        
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .size(36.dp),
            color = Color.White,
            strokeWidth = 3.dp
        )
    }
}
