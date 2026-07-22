package com.example.voca.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.R
import com.example.voca.data.model.Word
import com.example.voca.data.model.WordUsage
import com.example.voca.ui.theme.*
import com.example.voca.ui.viewmodel.WordUiState
import com.example.voca.ui.viewmodel.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    wordId: String?,
    viewModel: WordViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val word = (uiState as? WordUiState.Success)?.words?.find { it.id == wordId }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
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
                                    text = "LEARN WORD",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VocaPinkDark,
                                    letterSpacing = 2.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(2.dp)
                                        .background(VocaPinkMain, RoundedCornerShape(1.dp))
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
        if (word == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Word not found", color = VocaTextGrey)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = word.term,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = VocaTextDark,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = word.phonetic,
                            fontSize = 20.sp,
                            color = VocaPinkDark,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )
                        
                        HorizontalDivider(color = VocaBg, thickness = 1.dp)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = word.definition,
                            fontSize = 18.sp,
                            color = VocaTextDark,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Usage Contexts",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = VocaTextDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                word.examples.forEachIndexed { index, usage ->
                    val bgColor = when (index % 3) {
                        0 -> AspectProfessional
                        1 -> AspectCasual
                        else -> AspectCreative
                    }
                    UsageAspectCard(usage, bgColor)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (word.isLearned) {
                    Button(
                        onClick = { onBackClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "ALREADY MASTERED",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Button(
                        onClick = { 
                            viewModel.markWordAsLearned(word)
                            onBackClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VocaPinkDark)
                    ) {
                        Text(
                            "MARK AS MASTERED",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun UsageAspectCard(usage: WordUsage, backgroundColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = usage.context.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = VocaPinkDark.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 10.dp),
                letterSpacing = 1.sp
            )
            Text(
                text = usage.sentence,
                fontSize = 17.sp,
                color = VocaTextDark,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = usage.explanation,
                    fontSize = 14.sp,
                    color = VocaTextDark.copy(alpha = 0.8f),
                    lineHeight = 20.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
