package com.example.voca.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voca.R
import com.example.voca.ui.theme.*
import com.example.voca.ui.viewmodel.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: WordViewModel) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }
    var showCountPicker by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
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
                                text = "SETTINGS",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                windowInsets = WindowInsets(0)
            )
        },
        containerColor = VocaBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingsSection(title = "LEARNING GOALS") {
                SettingsItem(
                    label = "Daily Word Count",
                    value = "${userPrefs.dailyWordCount} words",
                    onClick = { showCountPicker = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VocaBg, thickness = 1.dp)
                SettingsItem(
                    label = "Reminder Time",
                    value = userPrefs.reminderTime,
                    onClick = { showTimePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "SUPPORT") {
                SettingsItem(label = "Version Info", value = "1.2.0")
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VocaBg, thickness = 1.dp)
                SettingsItem(label = "Help & Support", showArrow = true)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VocaBg, thickness = 1.dp)
                SettingsItem(label = "Terms of Service", showArrow = true)
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { showClearConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All Progress", color = Color.Red, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Reminder Time") },
            text = {
                Column {
                    listOf("8:00 AM", "9:00 AM", "10:00 AM", "7:00 PM", "8:00 PM").forEach { time ->
                        TextButton(
                            onClick = {
                                viewModel.updateReminderTime(time)
                                showTimePicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(time, color = VocaPinkDark)
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color.White
        )
    }

    if (showCountPicker) {
        AlertDialog(
            onDismissRequest = { showCountPicker = false },
            title = { Text("Daily Word Goal") },
            text = {
                Column {
                    listOf(5, 10, 15, 20, 25).forEach { count ->
                        TextButton(
                            onClick = {
                                viewModel.updateDailyWordCount(count)
                                showCountPicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$count words", color = VocaPinkDark)
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color.White
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color.Red) },
            title = { Text("Reset Progress?") },
            text = { Text("This will permanently reset your streaks, total words learned, and history. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllProgress()
                    showClearConfirm = false
                }) {
                    Text("Reset Everything", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = VocaTextGrey)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = VocaTextGrey,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    label: String,
    value: String? = null,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = VocaTextDark, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(text = value, fontSize = 15.sp, color = VocaPinkDark, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 4.dp))
            }
            if (showArrow || value != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = VocaTextGrey.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
