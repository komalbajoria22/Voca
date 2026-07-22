package com.example.voca.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.R
import com.example.voca.ui.theme.*
import com.example.voca.ui.viewmodel.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: WordViewModel,
    onHistoryClick: () -> Unit
) {
    val userPrefs by viewModel.userPreferences.collectAsState()

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
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "MY PROGRESS",
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    windowInsets = WindowInsets(0)
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Weekly Performance",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VocaTextDark
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PerformanceChartCard(weeklyProgress = userPrefs.weeklyProgress)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Streaks & Achievements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VocaTextDark
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AchievementItem(
                icon = Icons.Default.LocalFireDepartment,
                iconColor = Color(0xFFFF9800),
                title = "${userPrefs.currentStreak}-Day Streak",
                subtitle = if (userPrefs.currentStreak > 0) "You're on fire! Keep it up." else "Start your learning streak today!",
                progress = if (userPrefs.currentStreak > 0) 1f else 0.05f
            )
            Spacer(modifier = Modifier.height(12.dp))
            AchievementItem(
                icon = Icons.Default.Star,
                iconColor = Color(0xFFFFD700),
                title = "Vocabulary Master",
                subtitle = "View history of ${userPrefs.totalWordsLearned} learned words",
                progress = (userPrefs.totalWordsLearned / 100f).coerceIn(0.05f, 1f),
                onClick = onHistoryClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            AchievementItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconColor = Color(0xFF4CAF50),
                title = "Goal Chaser",
                subtitle = "Reached daily goal ${userPrefs.weeklyProgress.count { it >= userPrefs.dailyWordCount }} times this week",
                progress = (userPrefs.weeklyProgress.count { it >= userPrefs.dailyWordCount } / 7f).coerceIn(0.05f, 1f)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PerformanceChartCard(weeklyProgress: List<Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Words Learned",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VocaTextDark
                )
                Text(
                    text = "This Week",
                    fontSize = 12.sp,
                    color = VocaTextGrey,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                val maxVal = (weeklyProgress.maxOrNull() ?: 1).coerceAtLeast(5)
                
                days.forEachIndexed { index, day ->
                    val learned = weeklyProgress.getOrElse(index) { 0 }
                    val barHeightFraction = (learned.toFloat() / maxVal).coerceIn(0.05f, 1f)
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = learned.toString(), 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold,
                            color = if (learned > 0) VocaPinkDark else VocaTextGrey.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .fillMaxHeight(barHeightFraction)
                                .background(
                                    if (learned > 0) VocaPinkMain else ProgressGrey, 
                                    RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day, 
                            fontSize = 12.sp, 
                            color = VocaTextGrey,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    progress: Float,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = iconColor, 
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    fontSize = 15.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = VocaTextDark
                )
                Text(
                    text = subtitle, 
                    fontSize = 13.sp, 
                    color = VocaTextGrey,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = iconColor,
                    trackColor = iconColor.copy(alpha = 0.1f)
                )
            }
            
            if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = VocaTextGrey.copy(alpha = 0.5f)
                )
            }
        }
    }
}
