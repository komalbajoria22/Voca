package com.example.voca.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.voca.data.model.Word
import com.example.voca.data.repository.UserPreferences
import com.example.voca.data.repository.UserPreferencesRepository
import com.example.voca.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class WordUiState {
    data object Loading : WordUiState()
    data class Success(val words: List<Word>) : WordUiState()
    data class Error(val message: String) : WordUiState()
}

class WordViewModel(
    private val wordRepository: WordRepository = WordRepository(),
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _rawWords = MutableStateFlow<List<Word>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val userPreferences: StateFlow<UserPreferences> = userPrefsRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(
                reminderTime = "9:00 AM",
                dailyWordCount = 10,
                totalWordsLearned = 0,
                currentStreak = 0,
                lastLearnedTimestamp = 0L,
                weeklyProgress = listOf(0, 0, 0, 0, 0, 0, 0),
                learnedToday = emptySet(),
                allLearnedWords = emptyList()
            )
        )

    val uiState: StateFlow<WordUiState> = combine(_rawWords, _isLoading, _errorMessage, userPreferences) { words, loading, error, prefs ->
        when {
            loading -> WordUiState.Loading
            error != null -> WordUiState.Error(error)
            else -> {
                val processedWords = words.map { word ->
                    word.copy(isLearned = prefs.learnedToday.contains(word.term))
                }
                WordUiState.Success(processedWords)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WordUiState.Loading)

    init {
        fetchDailyWords()
    }

    fun fetchDailyWords() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Default to 10 if not set, or use preference
                val count = userPreferences.value.dailyWordCount
                val words = wordRepository.getDailyWords(count)
                _rawWords.value = words
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown Error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markWordAsLearned(word: Word) {
        viewModelScope.launch {
            userPrefsRepository.markWordAsLearned(word)
        }
    }

    fun updateReminderTime(time: String) {
        viewModelScope.launch {
            userPrefsRepository.updateReminderTime(time)
        }
    }

    fun updateDailyWordCount(count: Int) {
        viewModelScope.launch {
            userPrefsRepository.updateDailyWordCount(count)
            val words = wordRepository.getDailyWords(count)
            _rawWords.value = words
        }
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            userPrefsRepository.clearProgress()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as Application
                WordViewModel(
                    wordRepository = WordRepository(),
                    userPrefsRepository = UserPreferencesRepository(application)
                )
            }
        }
    }
}
