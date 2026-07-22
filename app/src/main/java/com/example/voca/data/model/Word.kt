package com.example.voca.data.model

data class Word(
    val id: String,
    val term: String,
    val definition: String,
    val phonetic: String,
    val examples: List<WordUsage> = emptyList(),
    val isLearned: Boolean = false
)

data class WordUsage(
    val context: String, // e.g., "Professional", "Casual", "Formal"
    val sentence: String,
    val explanation: String
)
