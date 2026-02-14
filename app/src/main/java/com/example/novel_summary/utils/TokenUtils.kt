package com.example.novel_summary.utils

import kotlin.math.roundToInt

object TokenUtils {
    // Token estimation: 1 token ≈ 4 characters (English)
    fun estimateTokensFromChars(chars: Int): Int = (chars / 4.0).roundToInt()
    fun estimateTokensFromText(text: String): Int = estimateTokensFromChars(text.length)

    // ✅ FIX: Match your BuildConfig values
    // Cerebras: 240,000 chars ≈ 60,000 tokens (under 65K limit)
    const val CEREBRAS_CHUNK_TOKENS = 60_000

    // Groq: 450,000 chars ≈ 112,500 tokens (under 128K limit)
    const val GROQ_CHUNK_TOKENS = 112_000

    // Convert tokens → chars
    fun tokensToChars(tokens: Int): Int = tokens * 4

    // Recommended char limits (should match BuildConfig)
    fun cerebrasMaxChars(): Int = tokensToChars(CEREBRAS_CHUNK_TOKENS)    // = 240,000
    fun groqMaxChars(): Int = tokensToChars(GROQ_CHUNK_TOKENS)            // = 448,000
}