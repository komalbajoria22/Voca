package com.example.voca.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.R
import com.example.voca.data.model.Word
import com.example.voca.data.repository.UserPreferences
import com.example.voca.ui.theme.*
import com.example.voca.ui.viewmodel.WordUiState
import com.example.voca.ui.viewmodel.WordViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListScreen(
    viewModel: WordViewModel,
    onWordClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 2.dp,
                color = Color.White
            ) {
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
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "VOCA",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VocaPinkDark,
                                    letterSpacing = 6.sp
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )
            }
        },
        containerColor = VocaBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Hello, Learner! 👋",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VocaTextDark,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Ready to master your ${userPrefs.dailyWordCount} daily words?",
                fontSize = 16.sp,
                color = VocaTextGrey,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            PerformanceCard(userPrefs = userPrefs)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Collection",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VocaTextDark
                )
                Surface(
                    color = VocaPinkMain.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${userPrefs.learnedToday.size}/${userPrefs.dailyWordCount}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = VocaPinkDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is WordUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = VocaPinkDark)
                    }
                    is WordUiState.Success -> {
                        WordGrid(words = state.words, onWordClick = onWordClick)
                    }
                    is WordUiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Oops! Something went wrong", color = VocaTextGrey)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceCard(userPrefs: UserPreferences) {
    val todayWordsLearned = userPrefs.learnedToday.size
    val goal = userPrefs.dailyWordCount
    val progress = if (goal > 0) (todayWordsLearned.toFloat() / goal).coerceAtMost(1f) else 0f
    val progressPercentage = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(VocaPinkDark, Color(0xFFFF85A1))
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = if (progress >= 1f) "Goal Reached! 🏆" else "Today's Progress",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "$todayWordsLearned / $goal words mastered",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "$progressPercentage%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun WordGrid(words: List<Word>, onWordClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        items(words) { word ->
            WordGridItem(word = word, onClick = { onWordClick(word.id) })
        }
    }
}

@Composable
fun WordGridItem(word: Word, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (word.isLearned) Color(0xFFF1F8E9) else Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 6.dp,
        border = if (word.isLearned) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC5E1A5)) else null
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word.term,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (word.isLearned) Color(0xFF388E3C) else VocaTextDark,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (word.isLearned) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = word.definition,
                fontSize = 12.sp,
                color = VocaTextGrey,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Text(
                text = if (word.isLearned) "REVIEW" else "LEARN NOW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (word.isLearned) Color(0xFF388E3C) else VocaPinkDark,
                letterSpacing = 1.sp
            )
        }
    }
}
