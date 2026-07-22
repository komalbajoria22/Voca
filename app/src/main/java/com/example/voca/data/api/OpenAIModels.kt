package com.example.voca.data.api

import com.google.gson.annotations.SerializedName

data class OpenAIRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<Message>,
    @SerializedName("response_format")
    val responseFormat: ResponseFormat? = null
)

data class Message(
    val role: String,
    val content: String
)

data class ResponseFormat(
    val type: String = "json_object"
)

data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class WordListResponse(
    val words: List<WordData>
)

data class WordData(
    val term: String,
    val definition: String,
    val phonetic: String,
    val examples: List<ExampleData>
)

data class ExampleData(
    val context: String,
    val sentence: String,
    val explanation: String
)
