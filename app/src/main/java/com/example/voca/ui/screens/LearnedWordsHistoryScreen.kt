package com.example.voca.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.R
import com.example.voca.data.model.Word
import com.example.voca.ui.theme.*
import com.example.voca.ui.viewmodel.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnedWordsHistoryScreen(
    viewModel: WordViewModel,
    onBackClick: () -> Unit
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val learnedWords = userPrefs.allLearnedWords

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp, color = Color.White) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "VOCABULARY MASTER",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VocaPinkDark,
                                    letterSpacing = 2.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(3.dp)
                                        .background(VocaPinkMain, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = VocaPinkDark
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )
            }
        },
        containerColor = VocaBg
    ) { padding ->
        if (learnedWords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No words learned yet. Start your journey!",
                    color = VocaTextGrey,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Your Collection (${learnedWords.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VocaTextDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                items(learnedWords) { word ->
                    HistoryWordItem(word)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryWordItem(word: Word) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = word.term,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VocaTextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = word.definition,
                fontSize = 14.sp,
                color = VocaTextGrey,
                lineHeight = 20.sp
            )
        }
    }
}
