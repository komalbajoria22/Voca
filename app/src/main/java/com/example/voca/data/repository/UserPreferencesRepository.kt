package com.example.voca.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.voca.data.model.Word
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Calendar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserPreferences(
    val reminderTime: String,
    val dailyWordCount: Int,
    val totalWordsLearned: Int,
    val currentStreak: Int,
    val lastLearnedTimestamp: Long,
    val weeklyProgress: List<Int>, // Mon (0) to Sun (6)
    val learnedToday: Set<String> = emptySet(),
    val allLearnedWords: List<Word> = emptyList()
)

class UserPreferencesRepository(private val context: Context) {

    private val gson = Gson()

    private object PreferencesKeys {
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val DAILY_WORD_COUNT = intPreferencesKey("daily_word_count")
        val TOTAL_WORDS_LEARNED = intPreferencesKey("total_words_learned")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LAST_LEARNED_TIMESTAMP = longPreferencesKey("last_learned_timestamp")
        val WEEKLY_PROGRESS = stringPreferencesKey("weekly_progress")
        val LEARNED_TODAY = stringSetPreferencesKey("learned_today")
        val ALL_LEARNED_WORDS = stringPreferencesKey("all_learned_words")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val weeklyProgressStr = preferences[PreferencesKeys.WEEKLY_PROGRESS] ?: "0,0,0,0,0,0,0"
            val weeklyProgress = try {
                weeklyProgressStr.split(",").map { it.toIntOrNull() ?: 0 }
            } catch (e: Exception) {
                listOf(0, 0, 0, 0, 0, 0, 0)
            }

            val lastTimestamp = preferences[PreferencesKeys.LAST_LEARNED_TIMESTAMP] ?: 0L
            val learnedToday = if (isToday(lastTimestamp)) {
                preferences[PreferencesKeys.LEARNED_TODAY] ?: emptySet()
            } else {
                emptySet()
            }

            val allLearnedWordsJson = preferences[PreferencesKeys.ALL_LEARNED_WORDS] ?: "[]"
            val allLearnedWords: List<Word> = try {
                val type = object : TypeToken<List<Word>>() {}.type
                gson.fromJson(allLearnedWordsJson, type)
            } catch (e: Exception) {
                emptyList()
            }

            UserPreferences(
                reminderTime = preferences[PreferencesKeys.REMINDER_TIME] ?: "9:00 AM",
                dailyWordCount = preferences[PreferencesKeys.DAILY_WORD_COUNT] ?: 10,
                totalWordsLearned = preferences[PreferencesKeys.TOTAL_WORDS_LEARNED] ?: 0,
                currentStreak = preferences[PreferencesKeys.CURRENT_STREAK] ?: 0,
                lastLearnedTimestamp = lastTimestamp,
                weeklyProgress = weeklyProgress,
                learnedToday = learnedToday,
                allLearnedWords = allLearnedWords
            )
        }

    suspend fun updateReminderTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME] = time
        }
    }

    suspend fun updateDailyWordCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_WORD_COUNT] = count
        }
    }

    suspend fun markWordAsLearned(word: Word) {
        context.dataStore.edit { preferences ->
            val learnedToday = (preferences[PreferencesKeys.LEARNED_TODAY] ?: emptySet()).toMutableSet()
            if (learnedToday.contains(word.term)) return@edit

            learnedToday.add(word.term)
            preferences[PreferencesKeys.LEARNED_TODAY] = learnedToday

            // Add to history list
            val allLearnedWordsJson = preferences[PreferencesKeys.ALL_LEARNED_WORDS] ?: "[]"
            val type = object : TypeToken<MutableList<Word>>() {}.type
            val allLearnedWordsList: MutableList<Word> = try {
                gson.fromJson(allLearnedWordsJson, type)
            } catch (e: Exception) {
                mutableListOf()
            }
            
            if (!allLearnedWordsList.any { it.term == word.term }) {
                allLearnedWordsList.add(word.copy(isLearned = true))
                preferences[PreferencesKeys.ALL_LEARNED_WORDS] = gson.toJson(allLearnedWordsList)
            }

            val currentTotal = preferences[PreferencesKeys.TOTAL_WORDS_LEARNED] ?: 0
            preferences[PreferencesKeys.TOTAL_WORDS_LEARNED] = currentTotal + 1

            // Update weekly progress
            val calendar = Calendar.getInstance()
            val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val weeklyProgressStr = preferences[PreferencesKeys.WEEKLY_PROGRESS] ?: "0,0,0,0,0,0,0"
            val weeklyProgressList = try {
                weeklyProgressStr.split(",").map { it.toIntOrNull() ?: 0 }.toMutableList()
            } catch (e: Exception) {
                mutableListOf(0, 0, 0, 0, 0, 0, 0)
            }
            
            while (weeklyProgressList.size < 7) weeklyProgressList.add(0)
            val trimmedList = weeklyProgressList.take(7).toMutableList()

            if (dayOfWeek in 0..6) {
                trimmedList[dayOfWeek] += 1
            }
            preferences[PreferencesKeys.WEEKLY_PROGRESS] = trimmedList.joinToString(",")

            // Streak Logic
            val lastTimestamp = preferences[PreferencesKeys.LAST_LEARNED_TIMESTAMP] ?: 0L
            val now = System.currentTimeMillis()
            
            if (lastTimestamp != 0L) {
                val lastCal = Calendar.getInstance().apply { timeInMillis = lastTimestamp }
                val todayCal = Calendar.getInstance().apply { timeInMillis = now }
                
                if (isYesterday(lastCal, todayCal)) {
                    val streak = preferences[PreferencesKeys.CURRENT_STREAK] ?: 0
                    preferences[PreferencesKeys.CURRENT_STREAK] = streak + 1
                } else if (!isSameDay(lastCal, todayCal)) {
                    preferences[PreferencesKeys.CURRENT_STREAK] = 1
                }
            } else {
                preferences[PreferencesKeys.CURRENT_STREAK] = 1
            }
            preferences[PreferencesKeys.LAST_LEARNED_TIMESTAMP] = now
        }
    }

    suspend fun clearProgress() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOTAL_WORDS_LEARNED] = 0
            preferences[PreferencesKeys.CURRENT_STREAK] = 0
            preferences[PreferencesKeys.LAST_LEARNED_TIMESTAMP] = 0L
            preferences[PreferencesKeys.WEEKLY_PROGRESS] = "0,0,0,0,0,0,0"
            preferences[PreferencesKeys.LEARNED_TODAY] = emptySet()
            preferences[PreferencesKeys.ALL_LEARNED_WORDS] = "[]"
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return isSameDay(cal1, cal2)
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(lastCal: Calendar, todayCal: Calendar): Boolean {
        val yesterday = todayCal.clone() as Calendar
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(lastCal, yesterday)
    }
}
