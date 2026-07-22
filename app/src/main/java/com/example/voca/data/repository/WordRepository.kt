package com.example.voca.data.repository

import android.util.Log
import com.example.voca.data.api.*
import com.example.voca.data.model.Word
import com.example.voca.data.model.WordUsage
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

class WordRepository {

    private val apiKey = "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"

    private val groqApi: OpenAIApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIApi::class.java)
    }

    suspend fun getDailyWords(count: Int = 10): List<Word> {
        val prompt = """
            Act as an AI Vocabulary Tutor using RAG principles for contextual learning.
            Generate $count sophisticated English words for today's lesson to help improve vocabulary.
            
            For each word, you MUST provide 3 distinct 'Usage Scenarios' that cover different aspects of life:
            1. Professional (Formal work/academic context)
            2. Social (Casual conversation with friends)
            3. Creative/Literature (Descriptive or poetic context)
            
            Format the output strictly as a JSON object. Do not wrap the response in markdown blocks. Return ONLY the raw JSON string:
            {
              "words": [
                {
                  "term": "the word",
                  "definition": "precise definition",
                  "phonetic": "/phonetic spelling/",
                  "examples": [
                    {
                      "context": "Context Title",
                      "sentence": "A natural sentence showing the word in action.",
                      "explanation": "Why this specific usage helps master the word's nuance."
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val request = OpenAIRequest(
            model = "llama-3.3-70b-versatile",
            messages = listOf(
                Message(role = "system", content = "You are a linguistics expert and AI tutor. Respond only with valid JSON structures without markdown blocks."),
                Message(role = "user", content = prompt)
            ),
            responseFormat = ResponseFormat(type = "json_object")
        )

        return try {
            val response = groqApi.getCompletion("Bearer $apiKey", request)
            val jsonContent = response.choices.firstOrNull()?.message?.content ?: ""
            val cleanJson = jsonContent.replace("```json", "").replace("```", "").trim()
            val wordListResponse = Gson().fromJson(cleanJson, WordListResponse::class.java)

            wordListResponse.words.map { wordData ->
                Word(
                    id = UUID.randomUUID().toString(),
                    term = wordData.term,
                    definition = wordData.definition,
                    phonetic = wordData.phonetic,
                    examples = wordData.examples.map { exampleData ->
                        WordUsage(
                            context = exampleData.context,
                            sentence = exampleData.sentence,
                            explanation = exampleData.explanation
                        )
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "General error: ${e.message}")
            getFallbackWords().take(count)
        }
    }

    private fun getFallbackWords(): List<Word> {
        return listOf(
            Word(UUID.randomUUID().toString(), "Resilient", "Able to withstand or recover quickly from difficult conditions.", "/rəˈzilyənt/", listOf(WordUsage("Professional", "The company remained resilient.", "Adaptability"))),
            Word(UUID.randomUUID().toString(), "Serendipity", "The occurrence of events by chance in a happy way.", "/ˌserənˈdipədē/", listOf(WordUsage("Social", "Pure serendipity.", "Happy coincidence"))),
            Word(UUID.randomUUID().toString(), "Eloquence", "Fluent or persuasive speaking.", "/ˈeləkwəns/", listOf(WordUsage("Professional", "Her eloquence impressed.", "Skill"))),
            Word(UUID.randomUUID().toString(), "Ephemeral", "Lasting for a very short time.", "/əˈfem(ə)rəl/", listOf(WordUsage("Creative", "Ephemeral beauty.", "Fleeting"))),
            Word(UUID.randomUUID().toString(), "Magnanimous", "Generous or forgiving.", "/maɡˈnanəməs/", listOf(WordUsage("Social", "Magnanimous in victory.", "Noble"))),
            Word(UUID.randomUUID().toString(), "Alacrity", "Brisk and cheerful readiness.", "/əˈlakrədē/", listOf(WordUsage("Social", "Accepted with alacrity.", "Readiness"))),
            Word(UUID.randomUUID().toString(), "Meticulous", "Showing great attention to detail.", "/məˈtikyələs/", listOf(WordUsage("Professional", "Meticulous planning.", "Careful"))),
            Word(UUID.randomUUID().toString(), "Peracity", "The quality of being persistent.", "/pər-ˈsa-sə-tē/", listOf(WordUsage("Professional", "His peracity paid off.", "Grit"))),
            Word(UUID.randomUUID().toString(), "Venerable", "Accorded a great deal of respect.", "/ˈven(ə)rəb(ə)l/", listOf(WordUsage("Creative", "A venerable institution.", "Respect"))),
            Word(UUID.randomUUID().toString(), "Zenith", "The time at which something is most powerful.", "/ˈzēnəTH/", listOf(WordUsage("Creative", "At the zenith of his career.", "Peak")))
        )
    }
}
