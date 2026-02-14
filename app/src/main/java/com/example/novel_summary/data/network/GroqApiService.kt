package com.example.novel_summary.data.network

import com.example.novel_summary.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeUnit

data class ChatCompletionRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<Message>,
    val temperature: Float = 0.7f,
    val max_tokens: Int = 2048,
    val top_p: Float = 1.0f
)

data class Message(
    val role: String,
    val content: String
)

data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Long,
    val model: String,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

interface GroqApiService {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

class ApiKeyInterceptor : Interceptor {
    private var currentKeyIndex = 0
    private val apiKeys = listOf(
        BuildConfig.GROQ_API_KEY_PRIMARY,
        BuildConfig.GROQ_API_KEY_FALLBACK
    ).filter { it.isNotBlank() }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response? = null
        var lastException: IOException? = null

        for (i in currentKeyIndex until apiKeys.size) {
            currentKeyIndex = i
            val apiKey = apiKeys[i]

            if (apiKey.isEmpty()) continue

            val originalRequest = chain.request()
            val requestWithAuth = originalRequest.newBuilder()
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .build()

            try {
                response = chain.proceed(requestWithAuth)

                // Retry on auth errors or rate limits
                if (response.code in listOf(401, 403, 429) && i < apiKeys.size - 1) {
                    response.close()
                    continue
                }

                return response
            } catch (e: IOException) {
                lastException = e
            }
        }

        throw lastException ?: IOException("All API keys failed")
    }
}

object GroqApi {
    // FIXED: Removed trailing spaces from BASE_URL
    private const val BASE_URL = "https://api.groq.com/openai/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GroqApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApiService::class.java)
    }
}